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
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.nuai.BuildConfig
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.MeasurementsLayoutBinding
import com.nuai.databinding.ScanByFaceActivityBinding
import com.nuai.history.viewmodel.HistoryViewModel
import com.nuai.home.model.ScanningResultData
import com.nuai.home.model.api.request.SendScanRequest
import com.nuai.network.ResponseStatus
import com.nuai.network.Status
import com.nuai.onboarding.ui.activity.TutorialActivity
import com.nuai.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ScanByFaceActivity : BaseActivity(), View.OnClickListener, HealthMonitorManagerInfoListener,
    StateListener,
    VitalSignsListener,
    ImageListener,
    AlertsListener {
    companion object {
        val tag: String = ScanByFaceActivity::class.java.simpleName
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
    private val historyViewModel: HistoryViewModel by viewModels()
    private var mManager: HealthMonitorManager? = null
    private var mSession: HealthMonitorSession? = null
    private var mTestMode: Enums.SessionMode = Enums.SessionMode.FACE
    private var mLicenseEnabledVitalSigns = HealthMonitorEnabledVitalSigns()
    private var mLicenseByActivation = false

    private var mTime = 0
    private var mTimeCountHandler: Handler? = null
    private var mWarningDialogTimeoutHandler: Handler? = null
    private var mMessageDialog: AlertDialog? = null
    private var progressPercent: Double = 0.0


    private var mDeviceEnabledVitalSigns: HealthMonitorEnabledVitalSigns? = null

    private var handlerPublishReport: Handler? = null
    private var isResultPublishedTimePassed = false
    private var isStopDialogVisible = false
    private var faceResultID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.scan_by_face_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.app_name))
        showToolbarIcon(true)
        initObserver()
        initClickListener()
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        binding.mainContent.measurementsLayout.readingProgressBar.setProgressPercentage(0.0, true)
        // Asking the user for Camera permission. without it, the SDK can't operate
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        } else {
            createHealthMonitorManager()
            createSession()
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            historyViewModel.sendScanResultState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }
                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.data != null && it.code == ResponseStatus.STATUS_CODE_SUCCESS) {
                            faceResultID = it.data.id
                            CommonUtils.showToast(this@ScanByFaceActivity, it.data.message)
                            finish()
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@ScanByFaceActivity, it.message)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (handlerPublishReport == null && isResultPublishedTimePassed) {
            showResultScreen(faceResultID)
        }
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            createSession()
        }
    }

    override fun onPause() {
        super.onPause()
        closeSession()
        updateUi(Enums.UiState.SCREEN_PAUSED)
        if (handlerPublishReport != null) {
            handlerPublishReport = null
            isResultPublishedTimePassed = false
            isStopDialogVisible = false
        }
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
                HealthMonitorCodes.MEASUREMENT_CODE_UNSUPPORTED_ORIENTATION_WARNING -> {
                    if (mSession != null && mSession?.state == SessionState.MEASURING) {
                        showWarning("" + warningData.code)
                    }
                    binding.mainContent.faceFrame.setImageResource(R.drawable.face_frame_error)
                }
                HealthMonitorCodes.MEASUREMENT_CODE_MISDETECTION_DURATION_EXCEEDS_LIMIT_WARNING -> {
                    resetMeasurements()
                    showWarning("" + warningData.code)
                }
                else -> showWarning("" + warningData.code)
            }
        }
    }

    override fun onError(errorData: ErrorData) {
        binding.mainContent.faceFrame.setImageResource(R.drawable.face_frame_normal)
        runOnUiThread {
            when (errorData.code) {
                HealthMonitorCodes.MEASUREMENT_CODE_UNSUPPORTED_ORIENTATION_WARNING -> {
                    if (mSession != null && mSession!!.state == SessionState.MEASURING) {
                        showWarning(getString(R.string.orientation_not_supported))
                    }
                    binding.mainContent.faceFrame.setImageResource(R.drawable.face_frame_error)
                }
                HealthMonitorCodes.MEASUREMENT_CODE_MISDETECTION_DURATION_EXCEEDS_LIMIT_ERROR -> {
                    stopMeasuring()
                    updateUi(Enums.UiState.MANUALLY_STOPPED)
                }
                HealthMonitorCodes.MEASUREMENT_CODE_INVALID_RECENT_DETECTION_RATE_ERROR -> {
                    stopMeasuring()
                    updateUi(Enums.UiState.MANUALLY_STOPPED)
                }
                HealthMonitorCodes.MEASUREMENT_CODE_LICENSE_ACTIVATION_FAILED_ERROR -> {
                    stopMeasuring()
                    updateUi(Enums.UiState.MANUALLY_STOPPED)
                }
                HealthMonitorCodes.DEVICE_CODE_TORCH_SHUT_DOWN_ERROR -> {
                    stopMeasuring()
                    updateUi(Enums.UiState.MANUALLY_STOPPED)
                }
                HealthMonitorCodes.MEASUREMENT_CODE_MISDETECTION_DURATION_EXCEEDS_LIMIT_WARNING -> {
                    binding.mainContent.faceFrame.setImageResource(R.drawable.face_frame_error)
                    resetMeasurements()
                    showWarning(getString(R.string.error_warning), errorData.code)
                }
            }
        }
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
            } else if (state == SessionState.STOPPING) {
                stopMeasuring()
            }
        }
    }

    // Implementation =============================================================================
    private fun handleVitalSign(vitalSign: VitalSign) {
        val measurementLayout: MeasurementsLayoutBinding = binding.mainContent.measurementsLayout
        Log.i(
            tag,
            "onVitalSign Message Type: " + vitalSign.type + " message: " + vitalSign.value
        )
        when (vitalSign.type) {
            VitalSignTypes.HEART_RATE -> {
                val heartRate = (vitalSign as VitalSignHeartRate).value
                measurementLayout.tvReading.text = heartRate?.toString() ?: "--"
            }
        }
    }


    private fun createHealthMonitorManager() {
        try {
            updateUi(Enums.UiState.LOADING)
            mManager = HealthMonitorManager(this, LicenseData(BuildConfig.LICENCE_KEY), this)
        } catch (e: HealthMonitorException) {
            showErrorDialog(e.errorCode, "HearMotionManager Error: (" + e.errorCode + ")")
        }
    }

    private fun createSession() {
        if (mManager == null || mSession != null) {
            return
        }
        updateUi(Enums.UiState.LOADING)
        try {
            if (mTestMode == Enums.SessionMode.FINGER) {
                mSession = mManager?.createFingerSessionBuilder(
                    baseContext,
                    AppConstant.BINAH_AI_SCANNING_TIME_SECONDS
                )?.withSessionStateListener(this@ScanByFaceActivity)
                    ?.withImageListener(this@ScanByFaceActivity)
                    ?.withVitalSignsListener(this@ScanByFaceActivity)
                    ?.withAlertsListener(this@ScanByFaceActivity)
                    ?.build()
//                mDeviceEnabledVitalSigns = mManager.getFingerEvaluationResult();
                Log.e(
                    tag,
                    "createSession called mDeviceEnabledVitalSigns: $mDeviceEnabledVitalSigns"
                )
            } else {
                val user = Pref.user
                var gender = Gender.MALE
                var age = 35.0
                var weight = 75.0
                if (user?.bodyInfo != null) {
                    gender = when (user.bodyInfo!!.gender!!.lowercase()) {
                        Enums.Gender.MALE.toString().lowercase() -> {
                            Gender.MALE
                        }
                        Enums.Gender.FEMALE.toString().lowercase() -> {
                            Gender.FEMALE
                        }
                        else -> {
                            Gender.UNSPECIFIED
                        }
                    }
                    if (user.bodyInfo?.weight != null)
                        weight = user.bodyInfo?.weight!!
                }
                val subjectDemographic = SubjectDemographic(gender, age, weight)
                mSession = mManager?.createFaceSessionBuilder(
                    baseContext,
                    AppConstant.BINAH_AI_SCANNING_TIME_SECONDS
                )
                    ?.withStateListener(this@ScanByFaceActivity)
                    ?.withImageListener(this@ScanByFaceActivity)
                    ?.withVitalSignsListener(this@ScanByFaceActivity)
                    ?.withAlertsListener(this@ScanByFaceActivity)
                    ?.withSubjectDemographic(subjectDemographic)
                    ?.build()
                mDeviceEnabledVitalSigns = null
            }
            Handler(Looper.getMainLooper()).postDelayed({
                if (mSession?.state != SessionState.MEASURING)
                    startMeasuring()
            }, 300)
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
            when (e.errorCode) {
                HealthMonitorCodes.DEVICE_CODE_MINIMUM_BATTERY_LEVEL_ERROR -> showErrorDialog(
                    e.errorCode,
                    getString(R.string.low_battery_error)
                )
                HealthMonitorCodes.DEVICE_CODE_LOW_POWER_MODE_ENABLED_ERROR -> showErrorDialog(
                    e.errorCode,
                    getString(R.string.power_save_error)
                )
                else -> showErrorDialog(e.errorCode, getString(R.string.cannot_start_session))
            }
        } catch (e: java.lang.IllegalStateException) {
            showWarning("Start Error - Session in illegal state")
        } catch (e: NullPointerException) {
            showWarning("Start Error - Session not initialized")
        }
    }

    private fun stopMeasuring() {
        try {
            if (mSession != null && mSession?.state == SessionState.MEASURING) {
                mSession?.stop()
            }
        } catch (e: java.lang.IllegalStateException) {
            showWarning("Stop Error - Session in illegal state")
        } catch (ignore: NullPointerException) {
            ignore.printStackTrace()
        }
    }

    private fun updateUi(state: Enums.UiState?) {
        when (state) {
            Enums.UiState.IDLE -> {
                if (binding.mainContent.cameraView.visibility == View.INVISIBLE) {
                    binding.mainContent.cameraView.visibility = View.VISIBLE
                    clearCanvas()
                }
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                stopTimeCount()
            }
            Enums.UiState.LOADING -> {
                binding.mainContent.cameraView.visibility = View.INVISIBLE
                stopTimeCount()
            }
            Enums.UiState.MEASURING -> {
                setupResultsUi()
                resetMeasurements()
                startTimeCount()
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                binding.mainContent.faceFrame.setImageResource(R.drawable.face_frame_normal)
                binding.mainContent.measurementsLayout.root.visibility = View.VISIBLE
            }
            Enums.UiState.MANUALLY_STOPPED -> {
//                binding.rippleStart.setVisibility(View.VISIBLE)
//                binding.simpleProgressBar.setVisibility(View.GONE)
//                binding.layoutCamera.setVisibility(View.VISIBLE)
//                binding.layoutMeasurementFace.setVisibility(View.GONE)
                if (mTestMode === Enums.SessionMode.FACE) {
//                    if (binding.roiWarning.getVisibility() === View.VISIBLE) {
//                        binding.roiWarning.setVisibility(View.GONE)
//                    }
                } else {
                    Log.e(tag, "Show finger ui")
                }
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                stopTimeCount()
            }
            Enums.UiState.MEASUREMENT_COMPLETED -> {
                stopMeasuring()
//                binding.rippleStart.setVisibility(View.VISIBLE)
//                binding.simpleProgressBar.setVisibility(View.GONE)
//                binding.layoutCamera.setVisibility(View.VISIBLE)
//                binding.layoutMeasurementFace.setVisibility(View.GONE)
//                if (mTestMode === SessionMode.FACE) {
//                    if (binding.roiWarning.getVisibility() === View.VISIBLE) {
//                        binding.roiWarning.setVisibility(View.GONE)
//                    }
//                } else {
//                    Log.e(com.nuai.ui.fragments.FragmentFace.tag, "Show finger ui")
//                }
                window
                    .clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                stopTimeCount()
            }
            Enums.UiState.SCREEN_PAUSED -> {
//                binding.rippleStart.setVisibility(View.VISIBLE)
//                binding.simpleProgressBar.setVisibility(View.GONE)
//                binding.layoutCamera.setVisibility(View.VISIBLE)
//                binding.layoutMeasurementFace.setVisibility(View.GONE)
//                if (mTestMode === SessionMode.FACE) {
//                    if (binding.roiWarning.getVisibility() === View.VISIBLE) {
//                        binding.roiWarning.setVisibility(View.GONE)
//                    }
//                } else {
//                    Log.e(com.nuai.ui.fragments.FragmentFace.tag, "Show finger ui")
//                }
//                if (binding.warningLayout.getVisibility() === View.VISIBLE) {
//                    binding.warningLayout.setVisibility(View.GONE)
//                }
            }
            Enums.UiState.SCREEN_RESUMED -> {
//                binding.rippleStart.setVisibility(View.GONE)
//                binding.simpleProgressBar.setVisibility(View.VISIBLE)
//                binding.layoutCamera.setVisibility(View.VISIBLE)
//                binding.layoutMeasurementFace.setVisibility(View.VISIBLE)
//                if (mTestMode === SessionMode.FACE) {
//                    if (binding.roiWarning.getVisibility() === View.VISIBLE) {
//                        binding.roiWarning.setVisibility(View.GONE)
//                    }
//                } else {
//                    Log.e(com.nuai.ui.fragments.FragmentFace.tag, "Show finger ui")
//                }
//                if (binding.warningLayout.getVisibility() === View.VISIBLE) {
//                    binding.warningLayout.setVisibility(View.GONE)
//                }
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
            binding.mainContent.faceFrame.setImageResource(R.drawable.face_frame_normal)
        } else {
            binding.mainContent.measurementsLayout.root.visibility = View.INVISIBLE
//            binding.mainContent.roiWarningText.setText(getString(if (mTestMode == Enums.SessionMode.FACE) R.string.no_face_detected else R.string.no_finger_detected))
        }
    }

    private fun resetMeasurements() {
        val measurementsBinding: MeasurementsLayoutBinding = binding.mainContent.measurementsLayout
        measurementsBinding.tvReading.text =
            if (mLicenseEnabledVitalSigns.isHeartRateEnabled) "--" else "N/A"
    }

    /*
       Method to check if device is enabled to capture vital signs or not
    */
    private fun setupResultsUi() {
        val enabledVitalSigns: HealthMonitorEnabledVitalSigns = resolveEnabledVitalSigns() ?: return
    }

    @SuppressLint("SetTextI18n")
    private fun handleFinalResults(finalResults: VitalSignsResults) {
        val enabledVitalSigns = resolveEnabledVitalSigns()
        val measurementsLayout: MeasurementsLayoutBinding = binding.mainContent.measurementsLayout
        if (enabledVitalSigns!!.isHeartRateEnabled) {
            val heartRate = finalResults.getResult(VitalSignTypes.HEART_RATE)
            if (heartRate?.value == null) {
                measurementsLayout.tvReading.text = "-"
            } else {
                measurementsLayout.tvReading.text = "" + heartRate.value
            }
        }
        binding.mainContent.measurementsLayout.root.visibility = View.VISIBLE

        val scanningResultData = ScanningResultData()


        if (finalResults.getResult(VitalSignTypes.HEART_RATE)?.value == null) {
            scanningResultData.heartRate = "0"
        } else {
            scanningResultData.heartRate =
                "" + finalResults.getResult(VitalSignTypes.HEART_RATE).value
        }

        if (finalResults.getResult(VitalSignTypes.OXYGEN_SATURATION)?.value == null) {
            scanningResultData.oxygenSaturation = "0"
        } else {
            scanningResultData.oxygenSaturation =
                "" + finalResults.getResult(VitalSignTypes.OXYGEN_SATURATION).value
        }

        if (finalResults.getResult(VitalSignTypes.BREATHING_RATE)?.value == null) {
            scanningResultData.respiration = "0"
        } else {
            scanningResultData.respiration =
                "" + finalResults.getResult(VitalSignTypes.BREATHING_RATE).value
        }

        if (finalResults.getResult(VitalSignTypes.HEMOGLOBIN)?.value == null) {
            scanningResultData.hemoglobin = "0"
        } else {
            scanningResultData.hemoglobin =
                "" + finalResults.getResult(VitalSignTypes.HEMOGLOBIN).value
        }

        if (finalResults.getResult(VitalSignTypes.HEMOGLOBIN_A1C)?.value == null) {
            scanningResultData.hba1c = "0"
        } else {
            scanningResultData.hba1c =
                "" + finalResults.getResult(VitalSignTypes.HEMOGLOBIN_A1C).value
        }

        if (finalResults.getResult(VitalSignTypes.SDNN)?.value == null) {
            scanningResultData.hrvSdnn = "0"
        } else {
            scanningResultData.hrvSdnn = "" + finalResults.getResult(VitalSignTypes.SDNN).value
        }
        if (finalResults.getResult(VitalSignTypes.STRESS_INDEX)?.value == null) {
            scanningResultData.stressLevel = "0"
        } else {
            scanningResultData.stressLevel =
                "" + (finalResults.getResult(VitalSignTypes.STRESS_INDEX) as VitalSignStressLevel).value
        }

        if (finalResults.getResult(VitalSignTypes.STRESS_LEVEL)?.value == null) {
            scanningResultData.stressResponse = ""
        } else {
            scanningResultData.stressResponse =
                "" + (finalResults.getResult(VitalSignTypes.STRESS_LEVEL) as VitalSignStressLevel).value
        }

        if (finalResults.getResult(VitalSignTypes.BLOOD_PRESSURE)?.value == null) {
            scanningResultData.bloodPressure = "0"
        } else {
            val bloodPressureValue =
                finalResults.getResult(VitalSignTypes.BLOOD_PRESSURE) as VitalSignBloodPressure
            scanningResultData.bloodPressure =
                "" + bloodPressureValue.value.systolic + "/" + bloodPressureValue.value.diastolic
        }
        if (finalResults.getResult(VitalSignTypes.PRQ)?.value == null) {
            scanningResultData.prq = "0"
        } else {
            scanningResultData.prq =
                "" + finalResults.getResult(VitalSignTypes.PRQ).value
        }

        if (finalResults.getResult(VitalSignTypes.BREATHING_RATE)?.value == null) {
            scanningResultData.breathingRate = "0"
        } else {
            scanningResultData.breathingRate =
                "" + finalResults.getResult(VitalSignTypes.BREATHING_RATE).value
        }
        if (finalResults.getResult(VitalSignTypes.WELLNESS_INDEX)?.value == null) {
            scanningResultData.wellnessScore = "0"
        } else {
            scanningResultData.wellnessScore =
                "" + finalResults.getResult(VitalSignTypes.WELLNESS_INDEX).value
        }

        scanningResultData.latitude = 0.0
        scanningResultData.longitude = 0.0

        if (handlerPublishReport != null) {
            handlerPublishReport!!.removeCallbacksAndMessages(null)
        }
        handlerPublishReport = Handler(Looper.getMainLooper())
        handlerPublishReport!!.postDelayed({
            isResultPublishedTimePassed = true
            if (handlerPublishReport != null) {
                val request = SendScanRequest(
                    scanningResultData.bloodPressure,
                    scanningResultData.hba1c,
                    scanningResultData.heartRate,
                    scanningResultData.hemoglobin,
                    scanningResultData.hrvSdnn,
                    scanningResultData.oxygenSaturation,
                    scanningResultData.respiration,
                    Enums.SessionMode.FACE.toString(),
                    scanningResultData.stressLevel,
                    scanningResultData.stressResponse,
                    scanningResultData.breathingRate,
                    scanningResultData.prq,
                    scanningResultData.wellnessScore,
                    0.0,
                    0.0
                )
                historyViewModel.sendScanResult(request)
            }
        }, AppConstant.RESULT_SCREEN_DELAY_TIME.toLong())
    }


    private fun showErrorDialog(code: Int) {
        if (mMessageDialog != null && mMessageDialog!!.isShowing) {
            return
        }
        mMessageDialog = AlertDialog.Builder(this)
            .setMessage(String.format(getString(R.string.error_message), code))
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    /*
       Method called to show error popup
    */
    private fun showErrorDialog(errorCode: Int, message: String) {
        /*Toast.makeText(requireActivity(), "ERROR: " + message, Toast.LENGTH_LONG).show();*/
        if (mMessageDialog != null && mMessageDialog!!.isShowing) {
            return
        }
        mMessageDialog =
            AlertDialog.Builder(this) //                .setMessage(message)
                .setMessage(
                    String.format(
                        getString(R.string.error_message),
                        message,
                        errorCode
                    )
                )
                .setPositiveButton(R.string.ok) { _, _ ->
                    //                        updateUi(UiState.SCREEN_PAUSED);
                }
                .show()
    }

    private fun showWarning(text: String) {
        showWarning(text, null)
    }

    private fun showWarning(text: String, errorCode: Int?) {
        var text: String? = text
        if (binding.crFaceNotDetectWarning.visibility == View.VISIBLE) {
            return
        }
        if (mWarningDialogTimeoutHandler != null) {
            mWarningDialogTimeoutHandler!!.removeCallbacksAndMessages(null)
        }
        if (errorCode != null) {
            text += " ($errorCode)"
        }
        binding.tvFaceNotDetectMsg.text = text
        binding.crFaceNotDetectWarning.visibility = View.VISIBLE
        mWarningDialogTimeoutHandler = Handler(Looper.getMainLooper())
        mWarningDialogTimeoutHandler!!.postDelayed(
            { binding.crFaceNotDetectWarning.visibility = View.INVISIBLE },
            2000
        )
    }

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

    private fun rescaleFaceRect(bitmap: Bitmap, faceRect: RectF): RectF {
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
        canvas?.drawColor(ContextCompat.getColor(baseContext, R.color.colorScreenBackground))
        if (canvas != null)
            binding.mainContent.cameraView.unlockCanvasAndPost(canvas)
    }

    /*
       Method to enable vital signs
    */
    private fun resolveEnabledVitalSigns(): HealthMonitorEnabledVitalSigns? {
        if (mDeviceEnabledVitalSigns == null) {
            mDeviceEnabledVitalSigns =
                HealthMonitorEnabledVitalSigns(true, true, true, true, true, true, true, true)
        }
        return if (mLicenseEnabledVitalSigns == null) {
            mDeviceEnabledVitalSigns
        } else HealthMonitorEnabledVitalSigns(
            mDeviceEnabledVitalSigns!!.isHeartRateEnabled && mLicenseEnabledVitalSigns.isHeartRateEnabled,
            mDeviceEnabledVitalSigns!!.isBreathingRateEnabled && mLicenseEnabledVitalSigns.isBreathingRateEnabled,
            mDeviceEnabledVitalSigns!!.isOxygenSaturationEnabled && mLicenseEnabledVitalSigns.isOxygenSaturationEnabled,
            mDeviceEnabledVitalSigns!!.isSdnnEnabled && mLicenseEnabledVitalSigns.isSdnnEnabled,
            mDeviceEnabledVitalSigns!!.isStressLevelEnabled && mLicenseEnabledVitalSigns.isStressLevelEnabled,
            mDeviceEnabledVitalSigns!!.isRRIntervalEnabled && mLicenseEnabledVitalSigns.isRRIntervalEnabled,
            mDeviceEnabledVitalSigns!!.isBloodPressureEnabled && mLicenseEnabledVitalSigns.isBloodPressureEnabled,
            mDeviceEnabledVitalSigns!!.isStressIndexEnabled && mLicenseEnabledVitalSigns.isStressIndexEnabled
        )
    }


    //    private fun showWarning(code: Int) {
//        showWarning(String.format(getString(R.string.warning_message), code))
//    }
    private fun initClickListener() {
        binding.onClickListener = this
        binding.mainContent.measurementsLayout.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.retry_btn -> {
                TutorialActivity.startActivity(this, TutorialActivity.Companion.From.SETTINGS)
            }
            R.id.stop_btn -> {
                stopTimeCount()
                stopMeasuring()
                closeSession()
                finish()
            }
        }
    }

    private fun showResultScreen(id: String?) {

        run {
//            openResultScreen(id, scanningResultData)
            isResultPublishedTimePassed = false
            isStopDialogVisible = false
        }
    }


    /*
       Method to show progress or timer when scanning is started
    */
    private fun startTimeCount() {
        Log.e(tag, "startTimeCount called")
        binding.mainContent.measurementsLayout.readingProgressBar.visibility = View.VISIBLE
        if (mTimeCountHandler != null) {
            mTimeCountHandler?.removeCallbacksAndMessages(null)
        }
        mTime = 0
        progressPercent = 0.0
        mTimeCountHandler = Handler(Looper.getMainLooper())
        mTimeCountHandler?.post(object : Runnable {
            override fun run() {

                mTime++
                try {
                    progressPercent =
                        ((mTime.toDouble() / AppConstant.BINAH_AI_SCANNING_TIME_SECONDS) * 100)
                    binding.mainContent.measurementsLayout.readingProgressBar.setProgressPercentage(
                        progressPercent, true
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mTimeCountHandler?.postDelayed(this, 1000)
            }
        })
    }

    /*
       Method to hide progress or timer when scanning is stopped
    */
    private fun stopTimeCount() {
        binding.mainContent.measurementsLayout.readingProgressBar.visibility = View.GONE
        mTimeCountHandler?.removeCallbacksAndMessages(null)
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