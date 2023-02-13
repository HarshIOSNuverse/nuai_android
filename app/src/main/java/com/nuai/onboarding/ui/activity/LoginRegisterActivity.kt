package com.nuai.onboarding.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.LoginRegisterActivityBinding
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