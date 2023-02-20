package com.nuai.home.ui.activity

import ai.binah.hrv.HealthMonitorManager
import ai.binah.hrv.HealthMonitorManager.HealthMonitorManagerInfoListener
import ai.binah.hrv.api.*
import ai.binah.hrv.api.HealthMonitorSession.SessionState
import ai.binah.hrv.api.HealthMonitorSession.StateListener
import ai.binah.hrv.api.v4.Gender
import ai.binah.hrv.api.v4.SubjectDemographic
import ai.binah.hrv.api.v4.alerts.ErrorData
import ai.binah.hrv.api.v4.alerts.WarningData
import ai.binah.hrv.api.v4.imagedata.ImageData
import ai.binah.hrv.api.v4.listeners.AlertsListener
import ai.binah.hrv.api.v4.listeners.ImageListener
import ai.binah.hrv.api.v4.listeners.VitalSignsListener
import ai.binah.hrv.api.v4.vitals.*
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.nuai.BuildConfig
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.MeasurementsLayoutBinding
import com.nuai.databinding.ScanByFaceActivityBinding
import com.nuai.onboarding.ui.activity.TutorialActivity
import com.nuai.profile.viewmodel.ProfileViewModel
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.Enums
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ScanByFaceActivity : BaseActivity(), View.OnClickListener, HealthMonitorManagerInfoListener,
    StateListener,
    VitalSignsListener,
    ImageListener,
    AlertsListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, ScanByFaceActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: ScanByFaceActivityBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private var mManager: HealthMonitorManager? = null
    private var mSession: HealthMonitorSession? = null
    private var mTestMode: Enums.SessionMode = Enums.SessionMode.FACE
    private var mLicenseEnabledVitalSigns = HealthMonitorEnabledVitalSigns()
    private var mLicenseByActivation = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.scan_by_face_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.app_name))
        showToolbarIcon(true)
        initClickListener()
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        binding.mainContent.measurementsLayout.readingProgressBar.setProgressPercentage(25.0, true)
        // Asking the user for Camera permission. without it, the SDK can't operate
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        } else {
            createHealthMonitorManager()
            createSession()
        }
    }

    override fun onResume() {
        super.onResume()
        createSession()
    }

    override fun onPause() {
        super.onPause()
        closeSession()
    }

    // HealthMonitorManager callbacks ============================================================
    override fun onHealthManagerReady() {
        //Deprecated
    }

    override fun onHealthManagerError(errorCode: Int, messageCode: Int) {
        showErrorDialog(messageCode)
        updateUi(Enums.UiState.IDLE)
    }

    override fun onSessionStateChange(session: HealthMonitorSession?, state: SessionState?) {
        //Deprecated
    }

    @SuppressLint("SetTextI18n")
    override fun onHealthMonitorManagerInfo(infoCode: Int, info: Any) {
        when (infoCode) {
            HealthMonitorInfoCodes.HEALTH_MONITOR_INFO_MEASUREMENT_COUNTING -> {
                mLicenseByActivation = true
            }
            HealthMonitorInfoCodes.HEALTH_MONITOR_INFO_LICENSE_ENABLED_VITAL_SIGNS -> {
                mLicenseEnabledVitalSigns = info as HealthMonitorEnabledVitalSigns
            }
            HealthMonitorInfoCodes.HEALTH_MONITOR_INFO_LICENSE_ACTIVATION -> {}
        }
    }


    // Listeners =================================================================================
    override fun onVitalSign(vitalSign: VitalSign) {
        runOnUiThread { handleVitalSign(vitalSign) }
    }

    override fun onFinalResults(results: VitalSignsResults) {
        runOnUiThread { handleFinalResults(results) }
    }

    override fun onWarning(warningData: WarningData) {
        runOnUiThread {
            when (warningData.code) {
                HealthMonitorCodes.MEASUREMENT_CODE_UNSUPPORTED_ORIENTATION_WARNING -> if (mSession != null && mSession?.state == SessionState.MEASURING) {
//                    showWarning(warningData.code)
                }
                HealthMonitorCodes.MEASUREMENT_CODE_MISDETECTION_DURATION_EXCEEDS_LIMIT_WARNING -> {
                    resetMeasurements()
//                    showWarning(warningData.code)
                }
//                else -> //showWarning(warningData.code)
            }
        }
    }

    override fun onError(errorData: ErrorData) {
        runOnUiThread { showErrorDialog(errorData.code) }
    }

    override fun onImage(imageData: ImageData) {
        runOnUiThread {
            handleImage(imageData.image, imageData.roiData.roi)
            handleRoiDetection(imageData.roiData.isRoiValid)
        }
    }

    // HealthMonitorSession.StateListener ========================================================
    override fun onStateChange(session: HealthMonitorSession?, state: SessionState) {
        runOnUiThread {
            if (state == SessionState.ACTIVE) {
                updateUi(Enums.UiState.IDLE)
            }
        }
    }

    // User Actions ===============================================================================
    fun startStopButtonClicked(view: View?) {
        if (mSession == null) {
            return
        }
        if (mSession?.state == SessionState.MEASURING) {
            stopMeasuring()
        } else {
            startMeasuring()
        }
    }

    fun changeModeClicked(view: View?) {
        try {
            closeSession()
            mTestMode =
                if (mTestMode == Enums.SessionMode.FACE) Enums.SessionMode.FINGER else Enums.SessionMode.FACE
            createSession()
        } catch (e: IllegalStateException) {
//            showWarning(getString(R.string.change_session_mode_error))
        }
    }

    // Implementation =============================================================================

    // Implementation =============================================================================
    private fun handleVitalSign(vitalSign: VitalSign) {
        val measurementLayout: MeasurementsLayoutBinding = binding.mainContent.measurementsLayout
        when (vitalSign.type) {
            VitalSignTypes.HEART_RATE -> {
                val heartRate = (vitalSign as VitalSignHeartRate).value
                measurementLayout.tvReading.text = heartRate?.toString() ?: "--"
            }
            VitalSignTypes.OXYGEN_SATURATION -> {
                val saturation = (vitalSign as VitalSignOxygenSaturation).value
//                measurementLayout.saturation.value.text = saturation?.toString() ?: "--"
            }
            VitalSignTypes.BREATHING_RATE -> {
                val respiration = (vitalSign as VitalSignBreathingRate).value
//                measurementLayout.breathingRate.value.text = respiration?.toString() ?: "--"
            }
        }
    }


    private fun createHealthMonitorManager() {
        try {
            updateUi(Enums.UiState.LOADING)
            mManager = HealthMonitorManager(this, LicenseData(BuildConfig.LICENCE_KEY), this)
        } catch (e: HealthMonitorException) {
            showErrorDialog(e.errorCode)
        }
    }

    private fun createSession() {
        if (mManager == null || mSession != null) {
            return
        }
        updateUi(Enums.UiState.LOADING)
        try {
            if (mTestMode == Enums.SessionMode.FINGER) {
                mSession = mManager?.createFingerSessionBuilder(baseContext, 120)
                    ?.withSessionStateListener(this@ScanByFaceActivity)
                    ?.withImageListener(this@ScanByFaceActivity)
                    ?.withVitalSignsListener(this@ScanByFaceActivity)
                    ?.withAlertsListener(this@ScanByFaceActivity)
                    ?.build()
            } else {
                /*
                 SubjectDemographic:
                 For accurate vital signs calculation the user's parameters should be provided.
                 The following is an example of how the parameters are passed to the SDK.
                 When measuring a new user then a new session must be created using the new user's parameters.
                 The parameters are used locally on the device only for the vital sign calculation, and deleted when the session is terminated.
                 */
                val subjectDemographic = SubjectDemographic(Gender.MALE, 35.0, 75.0)
                mSession = mManager?.createFaceSessionBuilder(baseContext, 120)
                    ?.withStateListener(this@ScanByFaceActivity)
                    ?.withImageListener(this@ScanByFaceActivity)
                    ?.withVitalSignsListener(this@ScanByFaceActivity)
                    ?.withAlertsListener(this@ScanByFaceActivity)
                    ?.withSubjectDemographic(subjectDemographic)
                    ?.build()
            }
        } catch (e: HealthMonitorException) {
            showErrorDialog(e.errorCode)
        }
    }

    private fun closeSession() {
        if (mSession != null) {
            mSession?.terminate()
            mSession?.removeStateListener(this)
        }
        mSession = null
    }

    private fun startMeasuring() {
        try {
            mSession?.start()
            updateUi(Enums.UiState.MEASURING)
        } catch (e: HealthMonitorException) {
            showErrorDialog(e.errorCode)
        } catch (e: java.lang.IllegalStateException) {
//            showWarning("Start Error - Session in illegal state")
        } catch (e: NullPointerException) {
//            showWarning("Start Error - Session not initialized")
        }
    }

    private fun stopMeasuring() {
        try {
            mSession?.stop()
        } catch (e: java.lang.IllegalStateException) {
//            showWarning("Stop Error - Session in illegal state")
        } catch (ignore: NullPointerException) {
        }
    }

    fun updateUi(state: Enums.UiState?) {
        when (state) {
            Enums.UiState.LOADING -> {
                binding.mainContent.cameraView.visibility = View.INVISIBLE
            }
            Enums.UiState.MEASURING -> {
                resetMeasurements()
//                binding.mainContent.playStopButton.setBackgroundResource(R.drawable.ic_stop_button)
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                binding.mainContent.measurementsLayout.root.visibility = View.VISIBLE
            }
            Enums.UiState.IDLE -> {
                if (binding.mainContent.cameraView.visibility === View.INVISIBLE) {
                    binding.mainContent.cameraView.visibility = View.VISIBLE
                    clearCanvas()
                }
//                binding.mainContent.playStopButton.isEnabled = true
//                binding.mainContent.playStopButton.setBackgroundResource(R.drawable.ic_play_button)
                if (mLicenseByActivation) {
                }
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            else -> {

            }
        }
    }

    private fun handleRoiDetection(detected: Boolean) {
        if (mSession == null || mSession!!.state != SessionState.MEASURING) {
            return
        }
        if (detected) {
            binding.mainContent.measurementsLayout.root.visibility = View.VISIBLE
        } else {
            binding.mainContent.measurementsLayout.root.visibility = View.INVISIBLE
//            binding.mainContent.roiWarningText.setText(getString(if (mTestMode == Enums.SessionMode.FACE) R.string.no_face_detected else R.string.no_finger_detected))
        }
    }

    private fun resetMeasurements() {
        val measurementsBinding: MeasurementsLayoutBinding = binding.mainContent.measurementsLayout
        measurementsBinding.tvReading.text =
            if (mLicenseEnabledVitalSigns.isHeartRateEnabled) "--" else "N/A"
//        measurementsBinding.saturation.value.text =
//            if (mLicenseEnabledVitalSigns.isOxygenSaturationEnabled) "--" else "N/A"
//        measurementsBinding.breathingRate.value.text =
//            if (mLicenseEnabledVitalSigns.isBreathingRateEnabled) "--" else "N/A"
//        measurementsBinding.sdnn.value.text =
//            if (mLicenseEnabledVitalSigns.isSdnnEnabled) "--" else "N/A"
//        measurementsBinding.stressLevel.value.text =
//            if (mLicenseEnabledVitalSigns.isStressLevelEnabled) "--" else "N/A"
//        measurementsBinding.bloodPressure.value.text =
//            if (mLicenseEnabledVitalSigns.isBloodPressureEnabled) "--" else "N/A"
    }

    private fun handleFinalResults(finalResults: VitalSignsResults) {
        val measurementsLayout: MeasurementsLayoutBinding = binding.mainContent.measurementsLayout
        if (mLicenseEnabledVitalSigns.isHeartRateEnabled) {
            val heartRate = finalResults.getResult(VitalSignTypes.HEART_RATE)
           measurementsLayout.tvReading.text =  if (heartRate == null) {
                "--"
            } else {
                heartRate.value.toString()
            }
        }
//        if (mLicenseEnabledVitalSigns.isOxygenSaturationEnabled) {
//            val oxygenSaturation = finalResults.getResult(VitalSignTypes.OXYGEN_SATURATION)
//            if (oxygenSaturation == null) {
//                measurementsLayout.saturation.value.text = "--"
//            } else {
//                measurementsLayout.saturation.value.text = oxygenSaturation.value.toString()
//            }
//        }
//        if (mLicenseEnabledVitalSigns.isBreathingRateEnabled) {
//            val breathingRate = finalResults.getResult(VitalSignTypes.BREATHING_RATE)
//            if (breathingRate == null) {
//                measurementsLayout.breathingRate.value.text = "--"
//            } else {
//                measurementsLayout.breathingRate.value.text = breathingRate.value.toString()
//            }
//        }
//        if (mLicenseEnabledVitalSigns.isSdnnEnabled) {
//            val sdnn = finalResults.getResult(VitalSignTypes.SDNN)
//            if (sdnn == null) {
//                measurementsLayout.sdnn.value.text = "--"
//            } else {
//                measurementsLayout.sdnn.value.text = sdnn.value.toString()
//            }
//        }
//        if (mLicenseEnabledVitalSigns.isStressLevelEnabled) {
//            val stressLevel =
//                finalResults.getResult(VitalSignTypes.STRESS_LEVEL) as VitalSignStressLevel?
//            if (stressLevel == null) {
//                measurementsLayout.stressLevel.value.text = "--"
//            } else {
//                measurementsLayout.stressLevel.value.text = stressLevel.value.toString()
//            }
//        }
//        if (mLicenseEnabledVitalSigns.isBloodPressureEnabled) {
//            val bloodPressure =
//                finalResults.getResult(VitalSignTypes.BLOOD_PRESSURE) as VitalSignBloodPressure?
//            if (bloodPressure == null) {
//                measurementsLayout.bloodPressure.value.text = "--"
//            } else {
//                measurementsLayout.bloodPressure.value.text = String.format(
//                    getString(R.string.blood_pressure_value),
//                    bloodPressure.value.systolic,
//                    bloodPressure.value.diastolic
//                )
//            }
//        }
        binding.mainContent.measurementsLayout.root.visibility = View.VISIBLE
        updateUi(Enums.UiState.IDLE)
    }


    private fun showErrorDialog(code: Int) {
//        if (mMessageDialog != null && mMessageDialog.isShowing()) {
//            return
//        }
        val mMessageDialog = AlertDialog.Builder(this)
            .setMessage(String.format(getString(R.string.error_message), code))
            .setPositiveButton(R.string.ok, null)
            .show()
    }

//    private fun showWarning(text: String) {
//        if (binding.mainContent.warningLayout.getVisibility() === View.VISIBLE) {
//            return
//        }
//        if (mWarningDialogTimeoutHandler != null) {
//            mWarningDialogTimeoutHandler.removeCallbacksAndMessages(null)
//        }
//        binding.mainContent.warningMessage.setText(text)
//        binding.mainContent.warningLayout.setVisibility(View.VISIBLE)
//        mWarningDialogTimeoutHandler = Handler(mainLooper)
//        mWarningDialogTimeoutHandler.postDelayed(Runnable {
//            binding.mainContent.warningLayout.setVisibility(
//                View.INVISIBLE
//            )
//        }, 5000)
//    }

    private fun handleImage(bitmap: Bitmap?, faceRect: RectF?) {
        if (bitmap == null) {
            return
        }
        val canvas: Canvas? = binding.mainContent.cameraView.lockCanvas()
        if (canvas != null) {
            if (mTestMode == Enums.SessionMode.FACE) {
                flipCanvas(canvas)
            }
            canvas.drawBitmap(
                bitmap,
                null,
                Rect(
                    0,
                    0,
                    binding.mainContent.cameraView.width,
                    binding.mainContent.cameraView.bottom - binding.mainContent.cameraView.top
                ),
                null
            )
            if (mTestMode == Enums.SessionMode.FACE && faceRect != null) {
                paintRect(canvas, rescaleFaceRect(bitmap, faceRect)!!)
            }
            binding.mainContent.cameraView.unlockCanvasAndPost(canvas)
        }
    }

    private fun flipCanvas(canvas: Canvas) {
        val matrix = Matrix()
        matrix.preScale(-1f, 1f)
        matrix.postTranslate(canvas.width.toFloat(), 0f)
        canvas.setMatrix(matrix)
    }

    private fun paintRect(canvas: Canvas, faceRect: RectF) {
        val paint = Paint(Paint.DITHER_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 12f
        paint.color = resources.getColor(R.color.faceRect, null)
        paint.strokeJoin = Paint.Join.MITER
        val path = Path()
        path.addRect(faceRect, Path.Direction.CW)
        canvas.drawPath(path, paint)
    }

    private fun rescaleFaceRect(bitmap: Bitmap, faceRect: RectF): RectF? {
        val rect = RectF(faceRect)
        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()
        val m = Matrix()
        m.postScale(1f, 1f, width / 2f, height / 2f)
        m.postScale(
            binding.mainContent.cameraView.width / width,
            binding.mainContent.cameraView.height / height
        )
        m.mapRect(rect)
        return rect
    }

    private fun clearCanvas() {
        val canvas: Canvas? = binding.mainContent.cameraView.lockCanvas()
        if (canvas != null) {
            canvas.drawColor(ContextCompat.getColor(baseContext, R.color.colorScreenBackground))
        }
        if (canvas != null)
            binding.mainContent.cameraView.unlockCanvasAndPost(canvas!!)
    }

    //    private fun showWarning(code: Int) {
//        showWarning(String.format(getString(R.string.warning_message), code))
//    }
    private fun initClickListener() {
        binding.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.retry_btn -> {
                TutorialActivity.startActivity(this, TutorialActivity.Companion.From.SETTINGS)
            }
            R.id.stop_btn -> {
                stopMeasuring()
                closeSession()
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createHealthMonitorManager()
            createSession()
        }
    }

}