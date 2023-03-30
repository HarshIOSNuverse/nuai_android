package com.checkmyself.onboarding.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.LoginRegisterActivityBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginRegisterActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, LoginRegisterActivity::class.java).run {
                activity.startActivity(this)
                activity.finish()
                activity.overridePendingTransition(0, 0)
            }
        }

        fun clearTopAndOpenLoginSignUpActivity(activity: Activity) {
            Intent(activity, LoginRegisterActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }.run {
                activity.startActivity(this)
                activity.finish()
            }
        }

    }

    private lateinit var binding: LoginRegisterActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.login_register_activity)
        initClickListener()

    }

    private fun initClickListener() {
        binding.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.create_account_btn -> {
                RegisterActivity.startActivity(this)
            }
            R.id.loginTxt -> {
                LoginActivity.startActivity(this)
            }
        }
    }
}