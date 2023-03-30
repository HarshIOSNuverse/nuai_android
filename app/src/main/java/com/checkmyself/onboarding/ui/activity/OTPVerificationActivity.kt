package com.checkmyself.onboarding.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.OtpVerificationActivityBinding
import com.checkmyself.interfaces.DialogClickListener
import com.checkmyself.network.ResponseStatus
import com.checkmyself.network.Status
import com.checkmyself.onboarding.viewmodel.OnBoardingViewModel
import com.checkmyself.utils.AlertDialogManager
import com.checkmyself.utils.AnimationsHandler
import com.checkmyself.utils.CommonUtils
import com.checkmyself.utils.IntentConstant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class OTPVerificationActivity : BaseActivity(), View.OnClickListener {
    companion object {
        object From {
            const val SIGNUP = 0
            const val FORGOT_PASSWORD = 1
            const val CHANGE_NUMBER = 3
            const val LOGIN = 4
        }

        fun startActivity(activity: Activity, email: String?) {
            Intent(activity, OTPVerificationActivity::class.java).apply {
                putExtra(IntentConstant.EMAIL, email)
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: OtpVerificationActivityBinding
    private val onBoardingViewModel: OnBoardingViewModel by viewModels()
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.otp_verification_activity)
        initClickListener()
        startCountDownTimer()
        initObserver()
        init()
    }

    private fun init() {
        email = intent?.getStringExtra(IntentConstant.EMAIL)
        onBoardingViewModel.from = From.FORGOT_PASSWORD
    }

    private fun initObserver() {
        lifecycleScope.launch {
            onBoardingViewModel.verifyActivationCodeState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }
                    Status.SUCCESS -> {
                        if (it.data != null && (it.code == ResponseStatus.STATUS_CODE_SUCCESS)) {
                            ResetPasswordActivity.startActivity(this@OTPVerificationActivity, email)
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        AlertDialogManager.showInformationDialog(this@OTPVerificationActivity,
                            icon = 0, msg = it.message,
                            button1Message = getString(R.string.continue_),
                            isClose = false, dialogClickListener = object : DialogClickListener {
                                override fun onButton1Clicked() {
                                }

                                override fun onButton2Clicked() {}
                                override fun onCloseClicked() {
                                }
                            })
                    }
                }
            }
        }
        lifecycleScope.launch {
            onBoardingViewModel.resendActivationCodeState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }
                    Status.SUCCESS -> {
                        if (it.data != null && (it.code == ResponseStatus.STATUS_CODE_SUCCESS)) {
                            showHideProgress(false)
                            AlertDialogManager.showInformationDialog(this@OTPVerificationActivity,
                                icon = 0, msg = it.data.message,
                                button1Message = getString(R.string.continue_),
                                isClose = false, dialogClickListener = object : DialogClickListener {
                                    override fun onButton1Clicked() {
                                    }

                                    override fun onButton2Clicked() {}
                                    override fun onCloseClicked() {
                                    }
                                })
                            startCountDownTimer()
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@OTPVerificationActivity, it.message)
                    }
                }
            }
        }
    }

    private var timer: CountDownTimer? = null
    private fun startCountDownTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
        setTimerText(60000)
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                setTimerText(millisUntilFinished)
                showResendButton(false)
            }

            override fun onFinish() {
                setTimerText(0)
                showResendButton(true)
            }
        }.start()
    }

    private fun stopCountDownTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun setTimerText(remainingTime: Long) {
        binding.timerText.text = SimpleDateFormat("00:ss").format(Date(remainingTime))
    }

    private fun showResendButton(show: Boolean) {
        binding.resendText.visibility = if (show) View.VISIBLE else View.GONE
        binding.timerText.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun initClickListener() {
        binding.onClickListener = this
        binding.lineField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enableDisableButton(
                    binding.resetPwdBtn,
                    binding.lineField.text.toString().trim().length == 6
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.reset_pwd_btn -> {
                onBoardingViewModel.verifyOTP(email, binding.lineField.text.toString().trim())
            }
            R.id.resend_text -> {
                onBoardingViewModel.resendOTP(email)
            }
        }
    }

    override fun onDestroy() {
        stopCountDownTimer()
        super.onDestroy()
    }

    override fun onBackPressed() {
    }
}