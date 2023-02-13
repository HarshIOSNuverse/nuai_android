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
import com.nuai.databinding.CreatePasswordActivityBinding
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.CommonUtils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreatePasswordActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, CreatePasswordActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: CreatePasswordActivityBinding
    private var isPasswordShow = false
    private var isConfirmPwdShow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.create_password_activity)
        initClickListener()

    }

    private fun initClickListener() {
        binding.onClickListener = this
        binding.passwordEdit.addTextChangedListener(textWatcher)
        binding.confirmPasswordEdit.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            enableDisableButton(
                binding.submitBtn, (binding.passwordEdit.text.toString().trim().isNotEmpty()
                        && binding.confirmPasswordEdit.text.toString().trim().isNotEmpty())
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
            R.id.iv_password -> {
                isPasswordShow = !isPasswordShow
                CommonUtils.updatePasswordView(binding.passwordEdit, isPasswordShow)
                CommonUtils.updatePasswordViewIcon(binding.ivPassword, isPasswordShow)
            }
            R.id.iv_confirm_password -> {
                isConfirmPwdShow = !isConfirmPwdShow
                CommonUtils.updatePasswordView(binding.confirmPasswordEdit, isConfirmPwdShow)
                CommonUtils.updatePasswordViewIcon(binding.ivConfirmPassword, isConfirmPwdShow)
            }

            R.id.submit_btn -> {
                binding.passwordDivider.setBackgroundColor(normalColor)
                binding.confirmPasswordDivider.setBackgroundColor(normalColor)
                binding.passwordErrorText.visibility = View.GONE
                binding.confirmPasswordErrorText.visibility = View.GONE
                val password = binding.passwordEdit.text.toString().trim()
                val confirmPwd = binding.confirmPasswordEdit.text.toString().trim()
                if (password.isEmpty()) {
                    binding.passwordErrorText.text = getString(R.string.pls_enter_password)
                    return
                } else if (!CommonUtils.isValidPassword(password)) {
                    binding.passwordDivider.setBackgroundColor(errorColor)
                    binding.passwordErrorText.text =
                        getString(R.string.password_didnt_match_the_guidelines)
                    binding.passwordErrorText.visibility = View.VISIBLE
                    return
                } else if (confirmPwd != password) {
                    binding.confirmPasswordDivider.setBackgroundColor(errorColor)
                    binding.confirmPasswordErrorText.text =
                        getString(R.string.password_didnt_match)
                    binding.confirmPasswordErrorText.visibility = View.VISIBLE
                    return
                } else {
                    EnterActivationCodeActivity.startActivity(this)
                }
            }
            R.id.forgot_pwd_text -> {
                ForgotPasswordActivity.startActivity(this)
            }
        }
    }
}