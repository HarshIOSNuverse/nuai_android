package com.checkmyself.home.ui.activity

import ai.binah.sdk.api.HealthMonitorException
import ai.binah.sdk.api.SessionEnabledVitalSigns
import ai.binah.sdk.api.alerts.AlertCodes
import ai.binah.sdk.api.alerts.ErrorData
import ai.binah.sdk.api.alerts.WarningData
import ai.binah.sdk.api.images.ImageData
import ai.binah.sdk.api.images.ImageListener
import ai.binah.sdk.api.images.ImageValidity
import ai.binah.sdk.api.license.LicenseDetails
import ai.binah.sdk.api.license.LicenseInfo
import ai.binah.sdk.api.session.Session
import ai.binah.sdk.api.session.SessionInfoListener
import ai.binah.sdk.api.session.SessionState
import ai.binah.sdk.api.vital_signs.VitalSign
import ai.binah.sdk.api.vital_signs.VitalSignTypes
import ai.binah.sdk.api.vital_signs.VitalSignsListener
import ai.binah.sdk.api.vital_signs.VitalSignsResults
import ai.binah.sdk.api.vital_signs.vitals.VitalSignBloodPressure
import ai.binah.sdk.api.vital_signs.vitals.VitalSignPNSZone
import ai.binah.sdk.api.vital_signs.vitals.VitalSignPRQ
import ai.binah.sdk.api.vital_signs.vitals.VitalSignPulseRate
import ai.binah.sdk.api.vital_signs.vitals.VitalSignRespirationRate
import ai.binah.sdk.api.vital_signs.vitals.VitalSignSDNN
import ai.binah.sdk.api.vital_signs.vitals.VitalSignSNSZone
import ai.binah.sdk.api.vital_signs.vitals.VitalSignStressLevel
import ai.binah.sdk.session.FaceSessionBuilder
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.checkmyself.BuildConfig
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.MeasurementsLayoutBinding
import com.checkmyself.databinding.ScanByFaceActivityBinding
import com.checkmyself.history.viewmodel.HistoryViewModel
import com.checkmyself.home.model.ScanningResultData
import com.checkmyself.home.model.api.request.SendScanRequest
import com.checkmyself.interfaces.DialogClickListener
import com.checkmyself.network.ResponseStatus
import com.checkmyself.network.Status
import com.checkmyself.utils.AlertDialogManager
import com.checkmyself.utils.AnimationsHandler
import com.checkmyself.utils.AppConstant
import com.checkmyself.utils.BinahErrorMessage
import com.checkmyself.utils.CommonUtils
import com.checkmyself.utils.Enums
import com.checkmyself.utils.Logger
import kotlinx.coroutines.launch


class ScanByFaceActivity1 : BaseActivity(), View.OnClickListener, ImageListener, VitalSignsListener, SessionInfoListener {

    companion object {
        val tag: String = ScanByFaceActivity1::class.java.simpleName
        fun startActivity(activity: Activity) {
            Intent(activity, ScanByFaceActivity1::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: ScanByFaceActivityBinding
    private val historyViewModel: HistoryViewModel by viewModels()
    private var handlerPublishReport: Handler? = null
    private var isResultPublishedTimePassed = false
    private var faceResultID: Long = 0
    private val permissionsRequestCode = 12345

    private var mTime = 0
    private var mTimeCountHandler: Handler? = null
    private var mWarningDialogTimeoutHandler: Handler? = null
    private var progressPercent: Double = 0.0

    private var session: Session? = null
    private var isErrorDialogShowing = false

    private val faceDetection: Bitmap? by lazy {
        ContextCompat.getDrawable(this, R.drawable.face)?.toBitmap()
    }

    private var enabledVitalSigns: SessionEnabledVitalSigns? = null


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

    private fun init() {
        binding.measurementsLayout.readingProgressBar.setProgressPercentage(0.0, true)
    }

    private fun initClickListener() {
        binding.onClickListener = this
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
                            MeasurementResultActivity.startActivity(
                                this@ScanByFaceActivity1, faceResultID
                            )
                            CommonUtils.showToast(this@ScanByFaceActivity1, it.data.message)
                            finish()
                            AnimationsHandler.playActivityAnimation(
                                this@ScanByFaceActivity1, AnimationsHandler.Animations.RightToLeft
                            )
                        }
                    }

                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@ScanByFaceActivity1, it.message)
                    }
                }
            }
        }
    }

    private fun createSession() {
        val licenseDetails = LicenseDetails(BuildConfig.LICENCE_KEY)
//        val subjectDemographic = SubjectDemographic(Sex.FEMALE, 35.0, 65.0)
        try {
            session = FaceSessionBuilder(applicationContext)
//                .withSubjectDemographic(subjectDemographic)
                .withImageListener(this).withVitalSignsListener(this).withSessionInfoListener(this).build(licenseDetails)

            Handler(Looper.getMainLooper()).postDelayed({
                if (session?.state == SessionState.READY) {
                    startMeasuring()
                } else {
                }
            }, 500)

        } catch (e: HealthMonitorException) {
            showErrorDialog(e.errorCode)
        }
    }

    private fun startMeasuring() {
        try {
            session?.start(AppConstant.BINAH_AI_SCANNING_TIME_SECONDS)
            startTimeCount()
        } catch (e: HealthMonitorException) {
            when (e.errorCode) {
                AlertCodes.DEVICE_CODE_MINIMUM_BATTERY_LEVEL_ERROR -> {
                    binding.measurementsLayout.root.visibility = View.INVISIBLE
                    AlertDialogManager.showInformationDialog(this@ScanByFaceActivity1,
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
                        })
                }

                AlertCodes.DEVICE_CODE_LOW_POWER_MODE_ENABLED_ERROR -> {
                    binding.measurementsLayout.root.visibility = View.INVISIBLE
                    AlertDialogManager.showInformationDialog(this@ScanByFaceActivity1,
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
                        })
                }

                else -> {
                    showWarning(BinahErrorMessage.getErrorMessage(e.errorCode))
                    hideReadingAndProgress()
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
        binding.measurementsLayout.tvScanningMsg.text = text
        mWarningDialogTimeoutHandler = Handler(Looper.getMainLooper())
        mWarningDialogTimeoutHandler!!.postDelayed(
            { }, 2000
        )
    }

    private fun hideReadingAndProgress() {
        binding.measurementsLayout.tvReading.visibility = View.GONE
        binding.measurementsLayout.tvReadingUnit.visibility = View.GONE
        binding.measurementsLayout.readingProgressBar.visibility = View.GONE
    }

    private fun showResultScreen(id: Long) {
        run {
            isResultPublishedTimePassed = false
        }
    }

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
                    progressPercent = ((mTime.toDouble() / AppConstant.BINAH_AI_SCANNING_TIME_SECONDS) * 100)
                    binding.measurementsLayout.readingProgressBar.setProgressPercentage(progressPercent, true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mTimeCountHandler?.postDelayed(this, 1000)
            }
        })
    }

    private fun stopMeasuring() {
        try {
            if (session != null) {
                session?.stop()
            }
        } catch (e: java.lang.IllegalStateException) {
            showWarning("Stop Error - Session in illegal state")
        } catch (ignore: NullPointerException) {
            ignore.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun closeSession() {
        if (session != null) {
            session?.terminate()
        }
        session = null
    }

    private fun resetMeasurements() {
        val measurementsBinding: MeasurementsLayoutBinding = binding.measurementsLayout
        measurementsBinding.tvReading.text = "--"
//            if (mLicenseEnabledVitalSigns.isHeartRateEnabled) "--" else getString(R.string.na)
    }


    private fun clearCanvas() {
        val canvas: Canvas? = binding.cameraView.lockCanvas()
        canvas?.drawColor(ContextCompat.getColor(baseContext, R.color.colorScreenBackground))
        if (canvas != null) binding.cameraView.unlockCanvasAndPost(canvas)
    }

    private var isDetected = false
    private fun updateReadingProgressMsg() {
        if (isDetected) {
            when (mTime) {
                in 0..5 -> {
                    binding.measurementsLayout.tvScanningMsg.text = getString(R.string.face_scanning_msg)
                }

                6 -> {
                    binding.measurementsLayout.tvScanningMsg.text = getString(R.string.sit_still_during_the_measurement)
                }

                in 7..12 -> {
                    binding.measurementsLayout.tvScanningMsg.text = getString(R.string.avoid_moving_or_talk)
                }

                in 13..18 -> {
                    binding.measurementsLayout.tvScanningMsg.text = getString(R.string.stay_focused_on_screen)
                }

                in 19..24 -> {
                    binding.measurementsLayout.tvScanningMsg.text = getString(R.string.wait_until_end_for_best_result)
                }

                in 25..30 -> {
                    binding.measurementsLayout.tvScanningMsg.text = getString(R.string.normal_prq_range)
                }

                in 31..36 -> {
                    binding.measurementsLayout.tvScanningMsg.text = getString(R.string.normal_resting_heart_rate)
                }

                in 37..42 -> {
                    binding.measurementsLayout.tvScanningMsg.text = getString(R.string.tracking_heart_rate_provide)
                }

                in 43..48 -> {
                    binding.measurementsLayout.tvScanningMsg.text = getString(R.string.athletes_may_track_hrv)
                }

                in 49..54 -> {
                    binding.measurementsLayout.tvScanningMsg.text = getString(R.string.high_levels_of_hrv)
                }

                in 55..60 -> {
                    binding.measurementsLayout.tvScanningMsg.text = getString(R.string.hold_on_all_the_result_will_appear)
                }
            }
        }
    }

    /*
       Method to hide progress or timer when scanning is stopped
    */
    private fun stopTimeCount() {
        binding.measurementsLayout.readingProgressBar.visibility = View.GONE
        mTimeCountHandler?.removeCallbacksAndMessages(null)
    }

    private var mMessageDialog: AlertDialog? = null
    private fun showErrorDialog(code: Int) {
        if ((mMessageDialog != null && mMessageDialog!!.isShowing) || code == 3500) {
            return
        }
        mMessageDialog = AlertDialog.Builder(this).setMessage(String.format(getString(R.string.error_message), code)).setPositiveButton(R.string.ok, null).show()
    }

    override fun onStart() {
        super.onStart()
        val permissionStatus = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            createSession()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), permissionsRequestCode)
        }
    }


    override fun onResume() {
        super.onResume()
        if (handlerPublishReport == null && isResultPublishedTimePassed) {
            showResultScreen(faceResultID)
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            session?.terminate()
            session = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionsRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createSession()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_stop -> {
                AlertDialogManager.showConfirmationDialog(this,
                    null,
                    getString(R.string.measurement_not_completed_msg),
                    button1Message = getString(R.string.yes),
                    button2Message = getString(R.string.no),
                    dialogClickListener = object : DialogClickListener {
                        override fun onButton1Clicked() {

                            stopTimeCount()
                            stopMeasuring()
//                            closeSession()
//                            finish()
                            AlertDialogManager.showInformationDialog(this@ScanByFaceActivity1,
                                0,
                                null,
                                getString(R.string.no_enough_data_was_recorded),
                                getString(R.string.try_again),
                                dialogClickListener = object : DialogClickListener {
                                    override fun onButton1Clicked() {
//                                        stopTimeCount()
//                                        stopMeasuring()
                                        closeSession()
                                        finish()
                                    }

                                    override fun onButton2Clicked() {
                                    }

                                    override fun onCloseClicked() {
                                    }
                                })
                        }

                        override fun onButton2Clicked() {
                        }

                        override fun onCloseClicked() {
                        }
                    })

            }
        }
    }

    override fun onImage(imageData: ImageData) {
        runOnUiThread {
//            handleImage(imageData.image, imageData.roi)
//            handleRoiDetection(imageData.imageValidity)

            isDetected = imageData.imageValidity == ImageValidity.VALID
//            Logger.e("isDetected in onImage == $isDetected")

            binding.cameraView.lockCanvas()?.let { canvas ->
                // Drawing the bitmap on the TextureView canvas
                val image = imageData.image
                canvas.drawBitmap(
                    image, null, Rect(0, 0, binding.cameraView.width, binding.cameraView.bottom - binding.cameraView.top), null
                )

                //Drawing the face detection (if not null..)
                imageData.roi?.let roi@{ faceDetectionRect ->
                    //First we scale the SDK face detection rectangle to fit the TextureView size
                    val targetRect = RectF(faceDetectionRect)
                    val m = Matrix()
                    m.postScale(1f, 1f, image.width / 2f, image.height / 2f)
                    m.postScale(
                        binding.cameraView.width.toFloat() / image.width.toFloat(), binding.cameraView.height.toFloat() / image.height.toFloat()
                    )
                    m.mapRect(targetRect)
                    // Then we draw it on the Canvas
                    canvas.drawBitmap(faceDetection ?: return@roi, null, targetRect, null)
                }
                binding.cameraView.unlockCanvasAndPost(canvas)
            }


            if (isDetected) {
                binding.measurementsLayout.root.visibility = View.VISIBLE
                binding.faceFrame.setImageResource(R.drawable.face_frame_normal)
                updateReadingProgressMsg()
                binding.measurementsLayout.tvScanningMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                binding.measurementsLayout.tvScanningMsg.setTextColor(ContextCompat.getColor(this, R.color.primary_text_color))
                binding.measurementsLayout.tvReading.visibility = View.VISIBLE
                binding.measurementsLayout.tvReadingUnit.visibility = View.VISIBLE
                binding.measurementsLayout.readingProgressBar.visibility = View.VISIBLE
            } else {

                var msg = getString(R.string.face_not_detected) + "\n\n" + getString(R.string.face_not_detected_msg)
                setSpannableColor(
                    binding.measurementsLayout.tvScanningMsg, msg, getString(R.string.face_not_detected), ContextCompat.getColor(this, R.color.error_msg_text_color)
                )

                when (imageData.imageValidity) {
                    ImageValidity.INVALID_DEVICE_ORIENTATION -> {
                        msg = getString(R.string.unsupported_orientation) + "\n\n" + getString(R.string.unsupported_orientation_msg)
                        setSpannableColor(
                            binding.measurementsLayout.tvScanningMsg, msg, getString(R.string.unsupported_orientation), ContextCompat.getColor(this, R.color.error_msg_text_color)
                        )
                    }

                    ImageValidity.INVALID_ROI -> {
                        msg = getString(R.string.face_not_detected) + "\n\n" + getString(R.string.face_not_detected_msg)
                        setSpannableColor(
                            binding.measurementsLayout.tvScanningMsg, msg, getString(R.string.face_not_detected), ContextCompat.getColor(this, R.color.error_msg_text_color)
                        )
                    }

                    ImageValidity.TILTED_HEAD -> {
                        msg = getString(R.string.tilted_head) + "\n\n" + getString(R.string.tilted_head_msg)
                        setSpannableColor(
                            binding.measurementsLayout.tvScanningMsg, msg, getString(R.string.tilted_head), ContextCompat.getColor(this, R.color.error_msg_text_color)
                        )
                    }

                    ImageValidity.FACE_TOO_FAR -> {
                        msg = getString(R.string.face_to_far) + "\n\n" + getString(R.string.face_to_far_msg)
                        setSpannableColor(
                            binding.measurementsLayout.tvScanningMsg, msg, getString(R.string.face_to_far), ContextCompat.getColor(this, R.color.error_msg_text_color)
                        )
                    }

                    ImageValidity.UNEVEN_LIGHT -> {
                        msg = getString(R.string.unenven_lighting) + "\n\n" + getString(R.string.unenven_lighting_msg)
                        setSpannableColor(
                            binding.measurementsLayout.tvScanningMsg, msg, getString(R.string.unenven_lighting), ContextCompat.getColor(this, R.color.error_msg_text_color)
                        )
                    }
                }
                binding.faceFrame.setImageResource(R.drawable.face_frame_error)
                binding.measurementsLayout.tvScanningMsg.setTextColor(ContextCompat.getColor(this, R.color.secondary_text_color))
                binding.measurementsLayout.tvReading.visibility = View.GONE
                binding.measurementsLayout.tvReadingUnit.visibility = View.GONE
                binding.measurementsLayout.readingProgressBar.visibility = View.GONE
            }
        }
    }

    private fun setSpannableColor(view: TextView, fulltext: String, subtext1: String, color: Int) {
        val str: Spannable = SpannableString(fulltext)
        val i1 = fulltext.indexOf(subtext1)
        str.setSpan(
            ForegroundColorSpan(color), i1, i1 + subtext1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        str.setSpan(
            AbsoluteSizeSpan(17, true), i1 + subtext1.length, fulltext.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        view.movementMethod = LinkMovementMethod.getInstance()
        view.text = str
        view.highlightColor = Color.TRANSPARENT
    }


    override fun onVitalSign(vitalSign: VitalSign?) {
        runOnUiThread {
            val measurementLayout: MeasurementsLayoutBinding = binding.measurementsLayout
            Log.i(
                tag, "onVitalSign Message Type: " + vitalSign?.type + " message: " + vitalSign?.value
            )
            when (vitalSign?.type) {
                VitalSignTypes.PULSE_RATE -> {
                    val pulseRate = (vitalSign as VitalSignPulseRate).value
                    measurementLayout.tvReading.text = pulseRate?.toString() ?: "--"
                }
//                VitalSignTypes.RESPIRATION_RATE -> {
//                    if (vitalSign.type == 2) {
//                        val bpm = vitalSign.value
//                        measurementLayout.tvReading.text = bpm?.toString() ?: "--"
//                    }
//                }
            }
        }
    }

    override fun onFinalResults(finalResults: VitalSignsResults) {
        runOnUiThread { handleFinalResults(finalResults) }
//        runOnUiThread {
//            val pulseRate = (finalResults?.getResult(VitalSignTypes.PULSE_RATE) as? VitalSignPulseRate)?.value ?: "N/A"
//            val bloodPressureValue = (finalResults?.getResult(VitalSignTypes.BLOOD_PRESSURE) as? VitalSignBloodPressure)?.let { bloodPressure ->
//                "${bloodPressure.value.systolic}/${bloodPressure.value.diastolic}"
//            } ?: "N/A"
//
//            Log.e("onFinalResults = ", "Pulse Rate: ${pulseRate}\nBlood Pressure: ${bloodPressureValue}")
//
//            if (handlerPublishReport != null) {
//                handlerPublishReport!!.removeCallbacksAndMessages(null)
//            }
//
//            stopTimeCount()
//        }

//        showAlert("Final Results", "Pulse Rate: $pulseRate\nBlood Pressure: $bloodPressureValue")
    }

    @SuppressLint("SetTextI18n")
    private fun handleFinalResults(finalResults: VitalSignsResults) {

//        val enabledVitalSigns = resolveEnabledVitalSigns()
        val measurementsLayout: MeasurementsLayoutBinding = binding.measurementsLayout
        if (enabledVitalSigns?.isEnabled(VitalSignTypes.PULSE_RATE) == true) {
            val bpm = finalResults.getResult(VitalSignTypes.PULSE_RATE)
            if (bpm?.value == null) {
                measurementsLayout.tvReading.text = "-"
            } else {
                measurementsLayout.tvReading.text = "" + bpm.value
            }
        }
        binding.measurementsLayout.root.visibility = View.VISIBLE

        val scanningResultData = ScanningResultData()

        Logger.e("finalResults == ${finalResults.results.toString()}")

        Log.d(
            "Final Result = HEART_RATE ", "" + finalResults.getResult(VitalSignTypes.PULSE_RATE)?.value
        )
        Log.d(
            "Final Result = OXYGEN_SATURATION", "" + finalResults.getResult(VitalSignTypes.OXYGEN_SATURATION)?.value
        )
        Log.d(
            "Final Result = HEMOGLOBIN ", "" + finalResults.getResult(VitalSignTypes.HEMOGLOBIN)?.value
        )
        Log.d(
            "Final Result = HEMOGLOBIN_A1C ", "" + finalResults.getResult(VitalSignTypes.HEMOGLOBIN_A1C)?.value
        )
        Log.d("Final Result = SDNN ", "" + finalResults.getResult(VitalSignTypes.SDNN)?.value)
        Log.d(
            "Final Result = STRESS_LEVEL ", "" + finalResults.getResult(VitalSignTypes.STRESS_LEVEL)?.value
        )
        Log.d(
            "Final Result = BLOOD_PRESSURE", "" + (finalResults.getResult(VitalSignTypes.BLOOD_PRESSURE) as VitalSignBloodPressure?)?.value?.systolic + "/" + (finalResults.getResult(
                VitalSignTypes.BLOOD_PRESSURE
            ) as VitalSignBloodPressure?)?.value?.systolic
        )
        Log.d("Final Result = PRQ ", "" + finalResults.getResult(VitalSignTypes.PRQ)?.value)
        Log.d(
            "Final Result = BREATHING_RATE ", "" + finalResults.getResult(VitalSignTypes.RESPIRATION_RATE)?.value
        )
        Log.d(
            "Final Result = WELLNESS_INDEX ", "" + finalResults.getResult(VitalSignTypes.WELLNESS_INDEX)?.value
        )
        Log.d(
            "Final Result = SNS_ZONE ", "" + finalResults.getResult(VitalSignTypes.SNS_ZONE)?.value
        )
        Log.d(
            "Final Result = PNS_ZONE ", "" + finalResults.getResult(VitalSignTypes.PNS_ZONE)?.value
        )

        if (finalResults.getResult(VitalSignTypes.PULSE_RATE)?.value == null) {
            scanningResultData.heartRate = "0"
        } else {
            scanningResultData.heartRate = "" + finalResults.getResult(VitalSignTypes.PULSE_RATE).value
        }

        var pulseRateConfidence: String? = ""
        var pulseRateConfidenceOrdinal: String? = ""
        if ((finalResults.getResult(VitalSignTypes.PULSE_RATE) as? VitalSignPulseRate)?.confidence?.level?.name != null) {
            pulseRateConfidence = (finalResults.getResult(VitalSignTypes.PULSE_RATE) as? VitalSignPulseRate)?.confidence?.level?.name
        }
        if ((finalResults.getResult(VitalSignTypes.PULSE_RATE) as? VitalSignPulseRate)?.confidence?.level?.ordinal != null) {
            pulseRateConfidenceOrdinal = (finalResults.getResult(VitalSignTypes.PULSE_RATE) as? VitalSignPulseRate)?.confidence?.level?.ordinal?.toString()
        }
        Logger.e("pulseRateConfidence = $pulseRateConfidence")
        Logger.e("pulseRateConfidenceOrdinal = $pulseRateConfidenceOrdinal")
        scanningResultData.heartRateConfLevel = pulseRateConfidenceOrdinal

        if (finalResults.getResult(VitalSignTypes.OXYGEN_SATURATION)?.value == null) {
            scanningResultData.oxygenSaturation = "0"
        } else {
            scanningResultData.oxygenSaturation = "" + finalResults.getResult(VitalSignTypes.OXYGEN_SATURATION).value
        }

        if (finalResults.getResult(VitalSignTypes.HEMOGLOBIN)?.value == null) {
            scanningResultData.hemoglobin = "0"
        } else {
            scanningResultData.hemoglobin = "" + finalResults.getResult(VitalSignTypes.HEMOGLOBIN).value
        }

        if (finalResults.getResult(VitalSignTypes.HEMOGLOBIN_A1C)?.value == null) {
            scanningResultData.hba1c = "0"
        } else {
            scanningResultData.hba1c = "" + finalResults.getResult(VitalSignTypes.HEMOGLOBIN_A1C).value
        }

        if (finalResults.getResult(VitalSignTypes.SDNN)?.value == null) {
            scanningResultData.hrvSdnn = "0"
        } else {
            scanningResultData.hrvSdnn = "" + finalResults.getResult(VitalSignTypes.SDNN).value
        }


        var sdnnConfidence: String? = ""
        var sdnnConfidenceOrdinal: String? = ""
        if ((finalResults.getResult(VitalSignTypes.SDNN) as? VitalSignSDNN)?.confidence?.level?.name != null) {
            sdnnConfidence = (finalResults.getResult(VitalSignTypes.SDNN) as? VitalSignSDNN)?.confidence?.level?.name
        }
        if ((finalResults.getResult(VitalSignTypes.SDNN) as? VitalSignSDNN)?.confidence?.level?.ordinal != null) {
            sdnnConfidenceOrdinal = (finalResults.getResult(VitalSignTypes.SDNN) as? VitalSignSDNN)?.confidence?.level?.ordinal?.toString()
        }
        Logger.e("sdnnConfidence = $sdnnConfidence")
        Logger.e("sdnnConfidenceOrdinal = $sdnnConfidenceOrdinal")
        scanningResultData.hrvSdnnConfLevel = sdnnConfidenceOrdinal

        if ((finalResults.getResult(VitalSignTypes.STRESS_LEVEL) as VitalSignStressLevel?)?.value == null || (finalResults.getResult(VitalSignTypes.STRESS_LEVEL) as VitalSignStressLevel?)?.value?.ordinal == 0) {
            scanningResultData.stressLevel = 0
        } else {
            scanningResultData.stressLevel = (finalResults.getResult(VitalSignTypes.STRESS_LEVEL) as VitalSignStressLevel).value.ordinal
        }

        if (finalResults.getResult(VitalSignTypes.BLOOD_PRESSURE)?.value == null) {
            scanningResultData.bloodPressure = "0"
        } else {
            val bloodPressureValue = finalResults.getResult(VitalSignTypes.BLOOD_PRESSURE) as VitalSignBloodPressure
            scanningResultData.bloodPressure = "" + bloodPressureValue.value.systolic + "/" + bloodPressureValue.value.diastolic
        }
        if (finalResults.getResult(VitalSignTypes.PRQ)?.value == null) {
            scanningResultData.prq = "0"
        } else {
            scanningResultData.prq = "" + finalResults.getResult(VitalSignTypes.PRQ).value
        }

        var prqConfidence: String? = ""
        var prqConfidenceOrdinal: String? = ""
        if ((finalResults.getResult(VitalSignTypes.PRQ) as? VitalSignPRQ)?.confidence?.level?.name != null) {
            prqConfidence = (finalResults.getResult(VitalSignTypes.PRQ) as? VitalSignPRQ)?.confidence?.level?.name
        }
        if ((finalResults.getResult(VitalSignTypes.PRQ) as? VitalSignPRQ)?.confidence?.level?.ordinal != null) {
            prqConfidenceOrdinal = (finalResults.getResult(VitalSignTypes.PRQ) as? VitalSignPRQ)?.confidence?.level?.ordinal?.toString()
        }
        Logger.e("prqConfidence = $prqConfidence")
        Logger.e("prqConfidenceOrdinal = $prqConfidenceOrdinal")
        scanningResultData.prqConfLevel = prqConfidenceOrdinal


        if (finalResults.getResult(VitalSignTypes.RESPIRATION_RATE)?.value == null) {
            scanningResultData.breathingRate = ""
        } else {
            scanningResultData.breathingRate = "" + finalResults.getResult(VitalSignTypes.RESPIRATION_RATE).value
        }

        var respirationRateConfidence: String? = ""
        var respirationRateConfidenceOrdinal: String? = ""
        if ((finalResults.getResult(VitalSignTypes.RESPIRATION_RATE) as? VitalSignRespirationRate)?.confidence?.level?.name != null) {
            respirationRateConfidence = (finalResults.getResult(VitalSignTypes.RESPIRATION_RATE) as? VitalSignRespirationRate)?.confidence?.level?.name
        }
        if ((finalResults.getResult(VitalSignTypes.RESPIRATION_RATE) as? VitalSignRespirationRate)?.confidence?.level?.ordinal != null) {
            respirationRateConfidenceOrdinal = (finalResults.getResult(VitalSignTypes.RESPIRATION_RATE) as? VitalSignRespirationRate)?.confidence?.level?.ordinal.toString()
        }
        Logger.e("respirationRateConfidence = $respirationRateConfidence")
        Logger.e("respirationRateConfidenceOrdinal = $respirationRateConfidenceOrdinal")
        scanningResultData.breathingRateConfLevel = respirationRateConfidenceOrdinal

        if (finalResults.getResult(VitalSignTypes.WELLNESS_INDEX)?.value == null) {
            scanningResultData.wellnessScore = ""
        } else {
            scanningResultData.wellnessScore = "" + finalResults.getResult(VitalSignTypes.WELLNESS_INDEX).value
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
                    Enums.SessionMode.FACE.toString(),
                    scanningResultData.stressLevel,
                    scanningResultData.stressResponse,
                    scanningResultData.breathingRate,
                    scanningResultData.prq,
                    scanningResultData.wellnessScore,
                    scanningResultData.recoveryAbility,

                    scanningResultData.heartRateConfLevel,
                    scanningResultData.breathingRateConfLevel,
                    scanningResultData.prqConfLevel,
                    scanningResultData.hrvSdnnConfLevel,
                )
                if (request.bloodPressure == "0" && request.heartRate == "0" && request.oxygenSaturation == "0" && request.prq == "0") {
//                    CommonUtils.showToast(this, getString(R.string.no_result_found))
                    Log.d(tag, "No result calculated")
                } else {
                    historyViewModel.sendScanResult(request)
                }
            }
        }, AppConstant.RESULT_SCREEN_DELAY_TIME.toLong())
    }

    override fun onSessionStateChange(sessionState: SessionState?) {
        Log.e("sessionState", "${sessionState?.name}")
        runOnUiThread {
            when (sessionState) {

                SessionState.INITIALIZING -> {
                    clearCanvas()
                }

                SessionState.READY -> {
//                    binding.startStopButton.isEnabled = true
//                    binding.startStopButton.text = getString(R.string.start)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }

                SessionState.PROCESSING -> {
//                    binding.startStopButton.isEnabled = true
//                    binding.startStopButton.text = getString(R.string.stop)
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }

                SessionState.STOPPING -> {
//                    stopMeasuring()
//                    stopTimeCount()
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }

                else -> {
//                    binding.startStopButton.isEnabled = false
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }

    override fun onWarning(warningData: WarningData) {
        runOnUiThread {
            when (warningData.code) {
                AlertCodes.INITIALIZATION_CODE_ROTATION_AND_ORIENTATION_MISMATCH -> {
                    if (session != null && session?.state == SessionState.PROCESSING) {
                        showWarning("" + warningData.code)
                    }
                    binding.faceFrame.setImageResource(R.drawable.face_frame_error)
                }

                AlertCodes.MEASUREMENT_CODE_MISDETECTION_DURATION_EXCEEDS_LIMIT_WARNING -> {
                    resetMeasurements()
                    showWarning("" + warningData.code)
                }

                else -> showWarning("" + warningData.code)
            }
        }
    }

    override fun onError(errorData: ErrorData) {
        runOnUiThread {
            try {
                Logger.e("onError == onError Called")
                binding.faceFrame.setImageResource(R.drawable.face_frame_normal)

                when (errorData.code) {
                    AlertCodes.INITIALIZATION_CODE_ROTATION_AND_ORIENTATION_MISMATCH -> {
                        if (session != null && session!!.state == SessionState.PROCESSING) {
                            showWarning(getString(R.string.orientation_not_supported))
                        }
                        binding.faceFrame.setImageResource(R.drawable.face_frame_error)
                    }

                    AlertCodes.MEASUREMENT_CODE_MISDETECTION_DURATION_EXCEEDS_LIMIT_ERROR -> {
                        stopMeasuring()
//                    updateUi(Enums.UiState.MANUALLY_STOPPED)
                        binding.measurementsLayout.root.visibility = View.INVISIBLE
                        if (!isErrorDialogShowing) {
                            isErrorDialogShowing = true
                            AlertDialogManager.showInformationDialog(this,
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
                                })
                        }
                    }

                    AlertCodes.MEASUREMENT_CODE_INVALID_RECENT_DETECTION_RATE_ERROR -> {
                        stopMeasuring()
                        stopTimeCount()
                    }

                    AlertCodes.MEASUREMENT_CODE_LICENSE_ACTIVATION_FAILED_ERROR -> {
                        stopMeasuring()
                        stopTimeCount()
                    }

                    AlertCodes.DEVICE_CODE_TORCH_SHUT_DOWN_ERROR -> {
                        stopMeasuring()
                        stopTimeCount()
                    }

                    AlertCodes.MEASUREMENT_CODE_MISDETECTION_DURATION_EXCEEDS_LIMIT_WARNING -> {
                        binding.faceFrame.setImageResource(R.drawable.face_frame_error)
                        resetMeasurements()
                        showWarning(getString(R.string.error_warning), errorData.code)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onLicenseInfo(licenseInfo: LicenseInfo) {
        runOnUiThread {
            licenseInfo.licenseActivationInfo.activationID.takeIf { it.isNotEmpty() }?.let { id ->
                try {
                    Log.i(tag, "License Activation ID: $id")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            licenseInfo.licenseOfflineMeasurements?.let { offlineMeasurements ->
                try {
                    Log.i(
                        tag, "License Offline Measurements: " + "${offlineMeasurements.totalMeasurements}/" + "${offlineMeasurements.remainingMeasurements}"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    override fun onEnabledVitalSigns(sessionEnabledVitalSigns: SessionEnabledVitalSigns) {
        runOnUiThread {
            enabledVitalSigns = sessionEnabledVitalSigns
            Log.i(
                tag, "Pulse Rate Enabled: ${sessionEnabledVitalSigns.isEnabled(VitalSignTypes.PULSE_RATE)}"
            )
        }
    }

}