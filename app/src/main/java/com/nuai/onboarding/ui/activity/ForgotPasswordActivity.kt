package com.nuai.onboarding.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.ForgotPasswordActivityBinding
import com.nuai.network.ResponseStatus
import com.nuai.network.Status
import com.nuai.onboarding.viewmodel.OnBoardingViewModel
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.CommonUtils
import com.nuai.utils.Pref
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


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
    private val onBoardingViewModel: OnBoardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.forgot_password_activity)
        initClickListener()
        initObserver()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            onBoardingViewModel.forgotPasswordState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }
                    Status.SUCCESS -> {
                        if (it.data != null
                            && (it.code == ResponseStatus.STATUS_CODE_SUCCESS
                                    || it.code == ResponseStatus.STATUS_CODE_CREATED)
                        ) {
//                            Pref.accessToken = it.data.accessToken
//                            onBoardingViewModel.getMe()
                            OTPVerificationActivity.startActivity(
                                this@ForgotPasswordActivity,
                                binding.emailEdit.text.toString().trim()
                            )
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        when (it.code) {
                            // Email not found
                            409, 424 -> {
                                showEmailError(it.message)
                            }
                            else -> {
                                CommonUtils.showToast(this@ForgotPasswordActivity, it.message)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showEmailError(message: String?) {
        val errorColor =
            ContextCompat.getColor(this@ForgotPasswordActivity, R.color.error_msg_text_color)
        binding.ivEmail.setImageResource(R.drawable.cross)
        binding.ivEmail.visibility =
            if (binding.emailEdit.text.toString().trim().isNotEmpty()) View.VISIBLE else View.GONE
        binding.ivEmail.isEnabled = true
        binding.emailDivider.setBackgroundColor(errorColor)
        binding.emailErrorText.text = message
        binding.emailErrorText.visibility = View.VISIBLE
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
                binding.emailErrorText.visibility = View.GONE
                val email = binding.emailEdit.text.toString().trim()
                if (email.isEmpty()) {
                    showEmailError(getString(R.string.pls_enter_email))
                    return
                } else if (!CommonUtils.isValidEmail(email)) {
                    showEmailError(getString(R.string.pls_enter_valid_email))
                    return
                } else {
                    Pref.accessToken = null
                    onBoardingViewModel.forgotPassword(email)
                }
            }
        }
    }
}