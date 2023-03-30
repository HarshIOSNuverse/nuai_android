package com.checkmyself.onboarding.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.ResetPasswordSuccessActivityBinding
import com.checkmyself.utils.AnimationsHandler
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ResetPasswordSuccessActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, ResetPasswordSuccessActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: ResetPasswordSuccessActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.reset_password_success_activity)
        initClickListener()
        enableDisableButton(binding.returnToLoginBtn, true)
    }

    private fun initClickListener() {
        binding.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.return_to_login_btn -> {
                LoginActivity.startActivityClearTop(this)
            }
        }
    }

    override fun onBackPressed() {}
}