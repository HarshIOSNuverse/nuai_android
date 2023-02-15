package com.nuai.onboarding.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.RegistrationCompletedActivityBinding
import com.nuai.utils.AnimationsHandler
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegistrationCompletedActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, RegistrationCompletedActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: RegistrationCompletedActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.registration_completed_activity)
        initClickListener()
        enableDisableButton(binding.continueBtn, true)
    }

    private fun initClickListener() {
        binding.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.continue_btn -> {
                TutorialActivity.startActivity(this, TutorialActivity.Companion.From.SIGNUP)
            }
        }
    }

    override fun onBackPressed() {}
}