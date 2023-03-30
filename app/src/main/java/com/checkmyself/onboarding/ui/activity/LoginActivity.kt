package com.checkmyself.onboarding.ui.activity

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
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.LoginActivityBinding
import com.checkmyself.home.ui.activity.HomeActivity
import com.checkmyself.network.ResponseStatus
import com.checkmyself.network.Status
import com.checkmyself.onboarding.model.api.request.LoginRequest
import com.checkmyself.onboarding.viewmodel.OnBoardingViewModel
import com.checkmyself.profile.viewmodel.ProfileViewModel
import com.checkmyself.utils.AnimationsHandler
import com.checkmyself.utils.CommonUtils
import com.checkmyself.utils.Pref
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, LoginActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }

        fun startActivityClearTop(activity: Activity) {
            Intent(activity, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }.run {
                activity.startActivity(this)
                activity.finish()
            }
        }
    }

    private lateinit var binding: LoginActivityBinding
    private val onBoardingViewModel: OnBoardingViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private var isPasswordShow: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.login_activity)
        initClickListener()
        initObserver()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            onBoardingViewModel.loginState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }
                    Status.SUCCESS -> {
                        if (it.data != null
                            && (it.code == ResponseStatus.STATUS_CODE_SUCCESS
                                    || it.code == ResponseStatus.STATUS_CODE_CREATED)
                        ) {
                            Pref.accessToken = it.data.accessToken
                            profileViewModel.getMe()
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        binding.emailErrorText.visibility = View.GONE
                        binding.passwordErrorText.visibility = View.GONE
                        when (it.code) {
                            // Email not found
                            409,424 -> {
                                showEmailError(it.message)
                            }
                            // Wrong password
                            417 -> {
                                showPasswordError(it.message)
                            }
                            else -> {
                                CommonUtils.showToast(this@LoginActivity, it.message)
                            }
                        }
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
                            if (!it.data.user?.emailVerifiedAt.isNullOrEmpty()) {
                                Pref.user = it.data.user
                                HomeActivity.startActivity(this@LoginActivity)
                            } else {
                                EnterActivationCodeActivity.startActivity(this@LoginActivity)
                            }
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@LoginActivity, it.message)
                    }
                }
            }
        }
    }

    private fun showEmailError(message: String?) {
        val errorColor =
            ContextCompat.getColor(this@LoginActivity, R.color.error_msg_text_color)
        binding.ivEmail.setImageResource(R.drawable.cross)
        binding.ivEmail.visibility =
            if (binding.emailEdit.text.toString().trim().isNotEmpty()) View.VISIBLE else View.GONE
        binding.ivEmail.isEnabled = true
        binding.emailDivider.setBackgroundColor(errorColor)
        binding.emailErrorText.text = message
        binding.emailErrorText.visibility = View.VISIBLE
    }

    private fun showPasswordError(message: String?) {
        val errorColor =
            ContextCompat.getColor(this@LoginActivity, R.color.error_msg_text_color)
        val normalColor =
            ContextCompat.getColor(this@LoginActivity, R.color.et_hint_color)
        binding.emailDivider.setBackgroundColor(normalColor)
        binding.ivEmail.setImageResource(R.drawable.tick)
        binding.ivEmail.visibility = View.VISIBLE
        binding.ivEmail.isEnabled = false
        binding.passwordDivider.setBackgroundColor(errorColor)
        binding.passwordErrorText.text = message
        binding.passwordErrorText.visibility = View.VISIBLE
    }

    private fun initClickListener() {
        binding.onClickListener = this
        binding.emailEdit.addTextChangedListener(textWatcher)
        binding.passwordEdit.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            enableDisableButton(
                binding.loginBtn, (binding.emailEdit.text.toString().trim().isNotEmpty()
                        && binding.passwordEdit.text.toString().trim().isNotEmpty())
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
            R.id.iv_password -> {
                isPasswordShow = !isPasswordShow
                CommonUtils.updatePasswordView(binding.passwordEdit, isPasswordShow)
                CommonUtils.updatePasswordViewIcon(binding.ivPassword, isPasswordShow)
            }

            R.id.login_btn -> {
                Pref.accessToken = null
                binding.emailDivider.setBackgroundColor(normalColor)
                binding.passwordDivider.setBackgroundColor(normalColor)
                binding.emailErrorText.visibility = View.GONE
                binding.passwordErrorText.visibility = View.GONE
                val email = binding.emailEdit.text.toString().trim()
                val password = binding.passwordEdit.text.toString().trim()
                if (email.isEmpty()) {
                    showEmailError(getString(R.string.pls_enter_email))
                    return
                } else if (!CommonUtils.isValidEmail(email)) {
                    showEmailError(getString(R.string.pls_enter_valid_email))
                    return
                } else if (password.isEmpty()) {
                    showPasswordError(getString(R.string.pls_enter_password))
                    return
                } else {
                    val request = LoginRequest(
                        binding.emailEdit.text.toString().trim(),
                        binding.passwordEdit.text.toString().trim()
                    )
                    onBoardingViewModel.performLogin(request)
                }
            }
            R.id.forgot_pwd_text -> {
                ForgotPasswordActivity.startActivity(this)
            }
            R.id.dont_have_account_text -> {
                RegisterActivity.startActivity(this)
                finish()
                AnimationsHandler.playActivityAnimation(
                    this, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }
}