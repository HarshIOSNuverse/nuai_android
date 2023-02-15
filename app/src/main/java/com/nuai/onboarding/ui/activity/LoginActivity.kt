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
import com.nuai.databinding.LoginActivityBinding
import com.nuai.home.ui.activity.HomeActivity
import com.nuai.network.ResponseStatus
import com.nuai.network.Status
import com.nuai.onboarding.model.api.request.LoginRequest
import com.nuai.onboarding.viewmodel.OnBoardingViewModel
import com.nuai.profile.viewmodel.ProfileViewModel
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.CommonUtils
import com.nuai.utils.Pref
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
                        CommonUtils.showToast(this@LoginActivity, it.message)
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
                } else if (password.isEmpty()) {
                    binding.emailErrorText.text = getString(R.string.pls_enter_password)
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