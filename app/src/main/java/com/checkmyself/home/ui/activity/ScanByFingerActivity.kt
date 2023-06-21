package com.checkmyself.home.ui.activity

import ai.binah.hrv.HealthMonitorManager
import ai.binah.hrv.HealthMonitorManager.HealthMonitorManagerInfoListener
import ai.binah.hrv.api.*
import ai.binah.hrv.api.HealthMonitorSession.SessionState
import ai.binah.hrv.api.HealthMonitorSession.StateListener
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
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.checkmyself.BuildConfig
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.MeasurementsLayoutBinding
import com.checkmyself.databinding.ScanByFingerActivityBinding
import com.checkmyself.history.viewmodel.HistoryViewModel
import com.checkmyself.home.model.ScanningResultData
import com.checkmyself.home.model.api.request.SendScanRequest
import com.checkmyself.interfaces.DialogClickListener
import com.checkmyself.network.ResponseStatus
import com.checkmyself.network.Status
import com.checkmyself.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ScanByFingerActivity : BaseActivity(), View.OnClickListener, HealthMonitorManagerInfoListener,
    StateListener,
    VitalSignsListener,
    ImageListener,
    AlertsListener {
    companion object {
        val tag: String = ScanByFingerActivity::class.java.simpleName
        fun startActivity(activity: Activity) {
            Intent(activity, ScanByFingerActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: ScanByFingerActivityBinding
    private val historyViewModel: HistoryViewModel by viewModels()
    private var mManager: HealthMonitorManager? = null
    private var mSession: HealthMonitorSession? = null
    private var mTestMode: Enums.SessionMode = Enums.SessionMode.FINGER
    private var mLicenseEnabledVitalSigns = HealthMonitorEnabledVitalSigns()
    private var mLicenseByActivation = false

    private var mTime = 0
    private var mTimeCountHandler: Handler? = null
    private var mWarningDialogTimeoutHandler: Handler? = null

    //    private var mMessageDialog: AlertDialog? = null
    private var progressPercent: Double = 0.0

    private var mDeviceEnabledVitalSigns: HealthMonitorEnabledVitalSigns? = null

    private var handlerPublishReport: Handler? = null
    private var isResultPublishedTimePassed = false
    private var isStopDialogVisible = false
    private var fingerResultID: Long = 0
    private var isErrorDialogShowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.scan_by_finger_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.app_name))
        showToolbarIcon(true)
        initObserver()
        initClickListener()
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        binding.measurementsLayout.readingProgressBar.setProgressPercentage(0.0, true)
        // Asking the user for Camera permission. without it, the SDK can't operate
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), RP_CAMERA)
        } else {
            createHealthMonitorManager()
//            createSession()
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
                            CommonUtils.showToast(this@ScanByFingerActivity, it.data.message)
                            fingerResultID = it.data.id
                            MeasurementResultActivity.startActivity(
                                this@ScanByFingerActivity,
                                fingerResultID
                            )
                            finish()
                            AnimationsHandler.playActivityAnimation(
                                this@ScanByFingerActivity,
                                AnimationsHandler.Animations.RightToLeft
                            )
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@ScanByFingerActivity, it.message)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (handlerPublishReport == null && isResultPublishedTimePassed) {
            showResultScreen(fingerResultID)
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
        CommonUtils.showToast(this, "$messageCode")
//        showErrorDialog(messageCode)
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
                    showWarning("" + warningData.code)
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
        runOnUiThread {
            when (errorData.code) {
                HealthMonitorCodes.MEASUREMENT_CODE_UNSUPPORTED_ORIENTATION_WARNING -> if (mSession != null && mSession!!.state == SessionState.MEASURING) {
                    showWarning(getString(R.string.orientation_not_supported))
                }
                HealthMonitorCodes.MEASUREMENT_CODE_MISDETECTION_DURATION_EXCEEDS_LIMIT_ERROR -> {
                    stopMeasuring()
                    updateUi(Enums.UiState.MANUALLY_STOPPED)
                    binding.measurementsLayout.root.visibility = View.INVISIBLE
                    if (!isErrorDialogShowing) {
                        isErrorDialogShowing = true
                        AlertDialogManager.showInformationDialog(
                            this,
                            0,
                            msg = getString(R.string.face_or_finger_not_detected_2_times_msg),
                            button1Message = getString(R.string.retry),
                            dialogClickListener = object : DialogClickListener {
                                override fun onButton1Clicked() {
                                    isErrorDialogShowing = false
                                    startMeasuring()
                                }

                                override fun onButton2Clicked() {
                                }

                                override fun onCloseClicked() {
                                    isErrorDialogShowing = false
                                    stopTimeCount()
                                    stopMeasuring()
                                    closeSession()
                                    finish()
                                }
                            }
                        )
                    }
                    /*AlertDialogManager.showInformationDialog(
                        this@ScanByFingerActivity,
                        0,
                        null,
                        getString(R.string.moving_or_disruption_identified_msg),
                        getString(R.string.retry),
                        dialogClickListener = object : DialogClickListener {
                            override fun onButton1Clicked() {
                                startMeasuring()
                            }

                            override fun onButton2Clicked() {
                            }

                            override fun onCloseClicked() {
                            }
                        }
                    )*/
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
        val measurementLayout: MeasurementsLayoutBinding = binding.measurementsLayout
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


    fun createHealthMonitorManager() {
        try {
            updateUi(Enums.UiState.LOADING)
            mManager = HealthMonitorManager(this, LicenseData(BuildConfig.LICENCE_KEY), this)
        } catch (e: HealthMonitorException) {
            CommonUtils.showToast(this, "HearMotionManager Error: (" + e.errorCode + ")")
//            showErrorDialog(e.errorCode, "HearMotionManager Error: (" + e.errorCode + ")")
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
                )?.withSessionStateListener(this@ScanByFingerActivity)
                    ?.withImageListener(this@ScanByFingerActivity)
                    ?.withVitalSignsListener(this@ScanByFingerActivity)
                    ?.withAlertsListener(this@ScanByFingerActivity)
                    ?.build()
//                mDeviceEnabledVitalSigns = mManager.getFingerEvaluationResult();
                Log.e(
                    tag,
                    "createSession called mDeviceEnabledVitalSigns: $mDeviceEnabledVitalSigns"
                )
            } else {
//                val user = Pref.user
//                var gender = Gender.MALE
//                var weight = 75.0
//                if (user?.bodyInfo != null) {
//                    gender = when (user.bodyInfo!!.gender!!.lowercase()) {
//                        Enums.Gender.MALE.toString().lowercase() -> {
//                            Gender.MALE
//                        }
//                        Enums.Gender.FEMALE.toString().lowercase() -> {
//                            Gender.FEMALE
//                        }
//                        else -> {
//                            Gender.UNSPECIFIED
//                        }
//                    }
//                    if (user.bodyInfo?.weight != null)
//                        weight = user.bodyInfo?.weight!!
//                }
//                val subjectDemographic = SubjectDemographic(gender, 35.0, weight)
                mSession = mManager?.createFaceSessionBuilder(
                    baseContext,
                    AppConstant.BINAH_AI_SCANNING_TIME_SECONDS
                )
                    ?.withStateListener(this@ScanByFingerActivity)
                    ?.withImageListener(this@ScanByFingerActivity)
                    ?.withVitalSignsListener(this@ScanByFingerActivity)
                    ?.withAlertsListener(this@ScanByFingerActivity)
//                    ?.withSubjectDemographic(subjectDemographic)
                    ?.build()
                mDeviceEnabledVitalSigns = null
            }
            Handler(Looper.getMainLooper()).postDelayed({
                if (mSession?.state != SessionState.MEASURING)
                    startMeasuring()
//                startTimeCount()
//                binding.measurementsLayout.root.visibility = View.VISIBLE
            }, 500)
        } catch (e: HealthMonitorException) {
            CommonUtils.showToast(this, "${e.errorCode}")
//            showErrorDialog(e.errorCode)
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
                HealthMonitorCodes.DEVICE_CODE_MINIMUM_BATTERY_LEVEL_ERROR -> {
                    binding.measurementsLayout.root.visibility = View.INVISIBLE
                    AlertDialogManager.showInformationDialog(
                        this@ScanByFingerActivity,
                        0,
                        getString(R.string.low_battery_error),
                        getString(R.string.low_battery_msg),
                        getString(R.string.try_again),
                        isClose = true,
                        dialogClickListener = object : DialogClickListener {
                            override fun onButton1Clicked() {
                                startMeasuring()
                            }

                            override fun onButton2Clicked() {
                            }

                            override fun onCloseClicked() {
                                stopTimeCount()
                                stopMeasuring()
                                closeSession()
                                finish()
                            }
                        }
                    )
//                    showErrorDialog(
//                        e.errorCode,
//                        getString(R.string.low_battery_error)
//                    )
                }
                HealthMonitorCodes.DEVICE_CODE_LOW_POWER_MODE_ENABLED_ERROR -> {
                    binding.measurementsLayout.root.visibility = View.INVISIBLE
                    AlertDialogManager.showInformationDialog(
                        this@ScanByFingerActivity,
                        0,
                        getString(R.string.low_power_mode_title),
                        getString(R.string.low_power_mode_msg),
                        getString(R.string.try_again),
                        isClose = true,
                        dialogClickListener = object : DialogClickListener {
                            override fun onButton1Clicked() {
                                startMeasuring()
                            }

                            override fun onButton2Clicked() {
                            }

                            override fun onCloseClicked() {
                                stopTimeCount()
                                stopMeasuring()
                                closeSession()
                                finish()
                            }
                        }
                    )
//                    showErrorDialog(
//                        e.errorCode,
//                        getString(R.string.power_save_error)
//                    )
                }
                else -> {
                    showWarning(BinahErrorMessage.getErrorMessage(e.errorCode))
                    hideReadingAndProgress()
//                    showErrorDialog(e.errorCode, getString(R.string.cannot_start_session))
                }
            }
        } catch (e: java.lang.IllegalStateException) {
            showWarning("Start Error - Session in illegal state")
            hideReadingAndProgress()
        } catch (e: NullPointerException) {
            showWarning("Start Error - Session not initialized")
            hideReadingAndProgress()
        }
    }

    private fun hideReadingAndProgress() {
        binding.measurementsLayout.tvReading.visibility = View.GONE
        binding.measurementsLayout.tvReadingUnit.visibility = View.GONE
        binding.measurementsLayout.readingProgressBar.visibility = View.GONE
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
                if (binding.cameraView.visibility == View.INVISIBLE) {
                    binding.cameraView.visibility = View.VISIBLE
                    clearCanvas()
                }
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                stopTimeCount()
            }
            Enums.UiState.LOADING -> {
                binding.cameraView.visibility = View.INVISIBLE
                stopTimeCount()
            }
            Enums.UiState.MEASURING -> {
                setupResultsUi()
                resetMeasurements()
                startTimeCount()
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                binding.measurementsLayout.root.visibility = View.VISIBLE
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
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                stopTimeCount()
            }
            Enums.UiState.SCREEN_PAUSED -> {
            }
            Enums.UiState.SCREEN_RESUMED -> {
            }
            else -> {

            }
        }
    }

    private var isDetected = false
    private fun handleRoiDetection(detected: Boolean) {
        if (mSession == null || mSession!!.state != SessionState.MEASURING) {
            return
        }
        isDetected = detected
        binding.measurementsLayout.root.visibility = View.VISIBLE
        binding.crFingerHint.visibility = View.GONE
        if (detected) {
            binding.measurementsLayout.root.visibility = View.VISIBLE
            binding.ivFingerMask.setImageResource(R.drawable.ic_finger_white_elipse)
            updateReadingProgressMsg()
            binding.measurementsLayout.tvScanningMsg.setTextColor(
                ContextCompat.getColor(this, R.color.primary_text_color)
            )
            binding.measurementsLayout.tvReading.visibility = View.VISIBLE
            binding.measurementsLayout.tvReadingUnit.visibility = View.VISIBLE
            binding.measurementsLayout.readingProgressBar.setProgressDrawableColor(
                ContextCompat.getColor(this, R.color.green_text_color)
            )

        } else {
//            binding.measurementsLayout.root.visibility = View.INVISIBLE
            binding.ivFingerMask.setImageResource(R.drawable.ic_finger_red_elipse)
            binding.measurementsLayout.tvScanningMsg.text =
                getString(R.string.could_not_collect_data_over_seconds)
            val msg =
                getString(R.string.could_not_collect_data_over_seconds) + "\n\n" + getString(R.string.pls_dont_remove_your_finger)
            setSpannableColor(
                binding.measurementsLayout.tvScanningMsg,
                msg,
                getString(R.string.pls_dont_remove_your_finger),
                ContextCompat.getColor(this, R.color.error_msg_text_color)
            )

            binding.measurementsLayout.tvReading.visibility = View.GONE
            binding.measurementsLayout.tvReadingUnit.visibility = View.GONE
            binding.measurementsLayout.readingProgressBar.setProgressDrawableColor(
                ContextCompat.getColor(this, R.color.error_msg_text_color)
            )
        }
    }

    private fun setSpannableColor(
        view: TextView, fulltext: String, subtext1: String, color: Int
    ) {
        val str: Spannable = SpannableString(fulltext)
        val i1 = fulltext.indexOf(subtext1)
        str.setSpan(
            ForegroundColorSpan(color), i1, i1 + subtext1.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
//        str.setSpan(
//            AbsoluteSizeSpan(17, true), i1 + subtext1.length, fulltext.length,
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
        view.movementMethod = LinkMovementMethod.getInstance()
        view.text = str
        view.highlightColor = Color.TRANSPARENT
    }

    private fun resetMeasurements() {
        val measurementsBinding: MeasurementsLayoutBinding = binding.measurementsLayout
        measurementsBinding.tvReading.text =
            if (mLicenseEnabledVitalSigns.isHeartRateEnabled) "--" else getString(R.string.na)
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
        val measurementsLayout: MeasurementsLayoutBinding = binding.measurementsLayout
        if (enabledVitalSigns!!.isHeartRateEnabled) {
            val heartRate = finalResults.getResult(VitalSignTypes.HEART_RATE)
            if (heartRate?.value == null) {
                measurementsLayout.tvReading.text = "-"
            } else {
                measurementsLayout.tvReading.text = "" + heartRate.value
            }
        }
        binding.measurementsLayout.root.visibility = View.VISIBLE

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

        if ((finalResults.getResult(VitalSignTypes.STRESS_LEVEL) as VitalSignStressLevel?)?.value == null
            || (finalResults.getResult(VitalSignTypes.STRESS_LEVEL) as VitalSignStressLevel?)?.value?.ordinal == 0
        ) {
            scanningResultData.stressLevel = 0
        } else {
            scanningResultData.stressLevel =
                (finalResults.getResult(VitalSignTypes.STRESS_LEVEL) as VitalSignStressLevel).value.ordinal
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
            scanningResultData.breathingRate = ""
        } else {
            scanningResultData.breathingRate =
                "" + finalResults.getResult(VitalSignTypes.BREATHING_RATE).value
        }
        if (finalResults.getResult(VitalSignTypes.WELLNESS_INDEX)?.value == null) {
            scanningResultData.wellnessScore = ""
        } else {
            scanningResultData.wellnessScore =
                "" + finalResults.getResult(VitalSignTypes.WELLNESS_INDEX).value
        }

        if (finalResults.getResult(VitalSignTypes.SNS_ZONE)?.value == null) {
            scanningResultData.stressResponse = ""
        } else {
            val value = (finalResults.getResult(VitalSignTypes.SNS_ZONE) as VitalSignSNSZone).value
            scanningResultData.stressResponse = if (value.name.length > 1) {
                value.name.substring(0, 1) + value.name.substring(1, value.name.length).lowercase()
            } else {
                CommonUtils.getStressResponseAndRecoveryAbility(value.ordinal)
            }
        }

        if (finalResults.getResult(VitalSignTypes.SNS_ZONE)?.value == null) {
            scanningResultData.recoveryAbility = ""
        } else {
            val value = (finalResults.getResult(VitalSignTypes.PNS_ZONE) as VitalSignPNSZone).value
            scanningResultData.recoveryAbility = if (value.name.length > 1) {
                value.name.substring(0, 1) + value.name.substring(1, value.name.length).lowercase()
            } else {
                CommonUtils.getStressResponseAndRecoveryAbility(value.ordinal)
            }
        }

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
                    Enums.SessionMode.FINGER.toString(),
                    scanningResultData.stressLevel,
                    scanningResultData.stressResponse,
                    scanningResultData.breathingRate,
                    scanningResultData.prq,
                    scanningResultData.wellnessScore,
                    scanningResultData.recoveryAbility
                )
                if (request.bloodPressure == "0" && request.heartRate == "0" && request.oxygenSaturation == "0" && request.prq == "0") {
//                    CommonUtils.showToast(this, getString(R.string.no_result_found))
                    Log.d(ScanByFaceActivity.tag, "No result calculated")
                } else {
                    historyViewModel.sendScanResult(request)
                }
            }
        }, AppConstant.RESULT_SCREEN_DELAY_TIME.toLong())
    }

//    private fun showErrorDialog(code: Int) {
//        if ((mMessageDialog != null && mMessageDialog!!.isShowing) || code == 3500) {
//            return
//        }
//        mMessageDialog = AlertDialog.Builder(this)
//            .setMessage(String.format(getString(R.string.error_message), code))
//            .setPositiveButton(R.string.ok, null)
//            .show()
//    }

//    private fun showErrorDialog(errorCode: Int, message: String) {
//        if ((mMessageDialog != null && mMessageDialog!!.isShowing) || errorCode == 3500) {
//            return
//        }
//        mMessageDialog =
//            AlertDialog.Builder(this).setMessage(
//                String.format(
//                    getString(R.string.error_code_message), errorCode, message
//                )
//            ).setPositiveButton(R.string.ok) { _, _ ->
//                //                        updateUi(UiState.SCREEN_PAUSED);
//            }.show()
//    }

    private fun showWarning(text: String) {
        showWarning(text, null)
    }

    private fun showWarning(text1: String, errorCode: Int?) {
        var text: String? = text1
        if (mWarningDialogTimeoutHandler != null) {
            mWarningDialogTimeoutHandler!!.removeCallbacksAndMessages(null)
        }
        if (errorCode != null) {
            text += " ($errorCode)"
        }
        if (errorCode != null) {
            text += " ($errorCode)"
        }
        binding.measurementsLayout.tvScanningMsg.text = text
        mWarningDialogTimeoutHandler = Handler(Looper.getMainLooper())
        mWarningDialogTimeoutHandler!!.postDelayed(
            {
            }, 1000
        )
    }

    private fun handleImage(bitmap: Bitmap?, faceRect: RectF?) {
        if (bitmap == null) {
            return
        }
        val canvas: Canvas? = binding.cameraView.lockCanvas()
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
                    binding.cameraView.width,
                    binding.cameraView.bottom - binding.cameraView.top
                ),
                null
            )
            if (mTestMode == Enums.SessionMode.FACE && faceRect != null) {
                paintRect(canvas, rescaleFaceRect(bitmap, faceRect))
            }
            binding.cameraView.unlockCanvasAndPost(canvas)
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
            binding.cameraView.width / width,
            binding.cameraView.height / height
        )
        m.mapRect(rect)
        return rect
    }

    private fun clearCanvas() {
        val canvas: Canvas? = binding.cameraView.lockCanvas()
        canvas?.drawColor(ContextCompat.getColor(baseContext, R.color.colorScreenBackground))
        if (canvas != null)
            binding.cameraView.unlockCanvasAndPost(canvas)
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

    private fun initClickListener() {
        binding.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_stop, R.id.stop_btn_1 -> {
                AlertDialogManager.showConfirmationDialog(
                    this,
                    null,
                    getString(R.string.measurement_not_completed_msg),
                    button1Message = getString(R.string.yes),
                    button2Message = getString(R.string.no),
                    dialogClickListener = object : DialogClickListener {
                        override fun onButton1Clicked() {
                            AlertDialogManager.showInformationDialog(
                                this@ScanByFingerActivity,
                                title = null,
                                msg = getString(R.string.no_enough_data_was_recorded),
                                button1Message = getString(R.string.try_again),
                                dialogClickListener = object : DialogClickListener {
                                    override fun onButton1Clicked() {
                                        stopTimeCount()
                                        stopMeasuring()
                                        closeSession()
                                        finish()
                                    }

                                    override fun onButton2Clicked() {
                                    }

                                    override fun onCloseClicked() {
                                    }
                                }
                            )
                        }

                        override fun onButton2Clicked() {
                        }

                        override fun onCloseClicked() {
                        }
                    }

                )
            }
        }
    }

    private fun showResultScreen(id: Long) {

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
        binding.measurementsLayout.readingProgressBar.visibility = View.VISIBLE
        if (mTimeCountHandler != null) {
            mTimeCountHandler?.removeCallbacksAndMessages(null)
        }
        mTime = 0
        progressPercent = 0.0
        mTimeCountHandler = Handler(Looper.getMainLooper())
        mTimeCountHandler?.post(object : Runnable {
            override fun run() {

                mTime++
                updateReadingProgressMsg()
                try {
                    progressPercent =
                        ((mTime.toDouble() / AppConstant.BINAH_AI_SCANNING_TIME_SECONDS) * 100)
                    binding.measurementsLayout.readingProgressBar.setProgressPercentage(
                        progressPercent, true
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mTimeCountHandler?.postDelayed(this, 1000)
            }
        })
    }

    private fun updateReadingProgressMsg() {
        if (isDetected) {
            when (mTime) {
                in 0..5 -> {
                    binding.measurementsLayout.tvScanningMsg.text =
                        getString(R.string.finger_scanning_msg)
                }
                6 -> {
                    binding.measurementsLayout.tvScanningMsg.text =
                        getString(R.string.sit_still_during_the_measurement)
                }
                in 7..12 -> {
                    binding.measurementsLayout.tvScanningMsg.text =
                        getString(R.string.symptoms_of_hypoxemia_include_wheezing)
                }
                in 13..18 -> {
                    binding.measurementsLayout.tvScanningMsg.text =
                        getString(R.string.prq_is_calculate_by_dividing_heart_rate)
                }
                in 19..24 -> {
                    binding.measurementsLayout.tvScanningMsg.text =
                        getString(R.string.wait_until_end_for_best_result)
                }
                in 25..30 -> {
                    binding.measurementsLayout.tvScanningMsg.text =
                        getString(R.string.normal_resting_heart_rate)
                }
                in 31..36 -> {
                    binding.measurementsLayout.tvScanningMsg.text =
                        getString(R.string.make_sure_that_your_finger_completely_covers_the_camera)
                }
                in 37..42 -> {
                    binding.measurementsLayout.tvScanningMsg.text =
                        getString(R.string.high_blood_pressure_may_exist_wth_no_noticeable)
                }
                in 43..48 -> {
                    binding.measurementsLayout.tvScanningMsg.text =
                        getString(R.string.normal_prq_is_4_to_5)
                }
                in 49..54 -> {
                    binding.measurementsLayout.tvScanningMsg.text =
                        getString(R.string.tracking_heart_rate_provide)
                }
                in 55..60 -> {
                    binding.measurementsLayout.tvScanningMsg.text =
                        getString(R.string.hold_on_all_the_result_will_appear)
                }
            }
        }
    }

    /*
       Method to hide progress or timer when scanning is stopped
    */
    private fun stopTimeCount() {
//        binding.measurementsLayout.readingProgressBar.visibility = View.GONE
        mTimeCountHandler?.removeCallbacksAndMessages(null)
    }
}