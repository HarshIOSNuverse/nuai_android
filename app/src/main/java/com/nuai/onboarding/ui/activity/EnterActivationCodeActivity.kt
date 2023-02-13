package com.nuai.onboarding.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.EnterActivationCodeActivityBinding
import com.nuai.utils.AnimationsHandler
import dagger.hilt.android.AndroidEntryPoint
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.enter_activation_code_activity)
        initClickListener()
        startCountDownTimer()
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
        binding.timerText.text = "${SimpleDateFormat("00:ss").format(Date(remainingTime))} sec"
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
                TutorialActivity.startActivity(this)
            }
            R.id.resend_text -> {
                startCountDownTimer()
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