package com.nuai.onboarding.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.ForgotPasswordActivityBinding
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.CommonUtils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, ForgotPasswordActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: ForgotPasswordActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.forgot_password_activity)
        initClickListener()

    }

    private fun initClickListener() {
        binding.onClickListener = this
        binding.emailEdit.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            enableDisableButton(
                binding.getOtpBtn, binding.emailEdit.text.toString().trim().isNotEmpty()
            )
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    override fun onClick(v: View?) {
        val errorColor = ContextCompat.getColor(this, R.color.error_msg_text_color)
        val normalColor = ContextCompat.getColor(this, R.color.et_hint_color)
        when (v!!.id) {
            R.id.ivBgImage -> {
                finish()
            }
            R.id.iv_email -> {
                binding.emailEdit.setText("")
                binding.emailDivider.setBackgroundColor(normalColor)
                binding.emailErrorText.visibility = View.GONE
                binding.ivEmail.visibility = View.GONE
            }
            R.id.get_otp_btn -> {
                binding.emailDivider.setBackgroundColor(normalColor)
                val email = binding.emailEdit.text.toString().trim()
                if (email.isEmpty()) {
                    binding.emailErrorText.text = getString(R.string.pls_enter_email)
                    return
                } else if (!CommonUtils.isValidEmail(email)) {
                    binding.ivEmail.setImageResource(R.drawable.cross)
                    binding.ivEmail.visibility = View.VISIBLE
                    binding.ivEmail.isEnabled = true
                    binding.emailDivider.setBackgroundColor(errorColor)
                    binding.emailErrorText.text = getString(R.string.pls_enter_valid_email)
                    binding.emailErrorText.visibility = View.VISIBLE
                    return
                } else {
                    OTPVerificationActivity.startActivity(this)
                }
            }
        }
    }
}