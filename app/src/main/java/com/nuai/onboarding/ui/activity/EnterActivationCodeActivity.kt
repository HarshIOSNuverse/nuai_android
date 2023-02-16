package com.nuai.onboarding.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.EnterActivationCodeActivityBinding
import com.nuai.interfaces.DialogClickListener
import com.nuai.network.ResponseStatus
import com.nuai.network.Status
import com.nuai.onboarding.viewmodel.OnBoardingViewModel
import com.nuai.profile.viewmodel.ProfileViewModel
import com.nuai.utils.AlertDialogManager
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.CommonUtils
import com.nuai.utils.Pref
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class EnterActivationCodeActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, EnterActivationCodeActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: EnterActivationCodeActivityBinding
    private val onBoardingViewModel: OnBoardingViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.enter_activation_code_activity)
        initClickListener()
        initObserver()
        startCountDownTimer()
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
                            profileViewModel.getMe()
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        AlertDialogManager.showInformationDialog(this@EnterActivationCodeActivity,
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
            profileViewModel.meApiState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }
                    Status.SUCCESS -> {
                        if (it.data != null && (it.code == ResponseStatus.STATUS_CODE_SUCCESS)) {
                            Pref.user = it.data.user
                            AcceptTermsAndConditionActivity.startActivity(this@EnterActivationCodeActivity)
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@EnterActivationCodeActivity, it.message)
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
                            AlertDialogManager.showInformationDialog(this@EnterActivationCodeActivity,
                                icon = 0,
                                msg = it.data.message,
                                button1Message = getString(R.string.continue_),
                                isClose = false,
                                dialogClickListener = object : DialogClickListener {
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
                        CommonUtils.showToast(this@EnterActivationCodeActivity, it.message)
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

    private fun showResendButton(isTimerCompleted: Boolean) {
        binding.resendText.isEnabled=isTimerCompleted
        if(isTimerCompleted){
            binding.resendText.setTextColor(ContextCompat.getColor(this,R.color.blue_text_color))
            binding.timerText.visibility = View.GONE
        }else{
            binding.resendText.setTextColor(ContextCompat.getColor(this,R.color.secondary_text_color))
            binding.timerText.visibility = View.VISIBLE
        }
    }

    private fun initClickListener() {
        binding.onClickListener = this
        binding.lineField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enableDisableButton(
                    binding.activateBtn,
                    binding.lineField.text.toString().trim().length == 6
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.activate_btn -> {
                onBoardingViewModel.verifyOTP(null, binding.lineField.text.toString().trim())
            }
            R.id.resend_text -> {
                onBoardingViewModel.resendOTP(null)
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