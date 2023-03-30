package com.checkmyself.onboarding.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.RegistrationCompletedActivityBinding
import com.checkmyself.utils.AnimationsHandler
import com.checkmyself.utils.Pref
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
        init()
    }

    private fun init() {
        val user = Pref.user
        if (!user?.firstName.isNullOrEmpty()) {
            binding.titleTxt.text = user?.firstName
        }
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