package com.nuai.settings.ui.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.SettingsActivityBinding
import com.nuai.network.ResponseStatus
import com.nuai.network.Status
import com.nuai.onboarding.ui.activity.TutorialActivity
import com.nuai.onboarding.ui.activity.WebActivity
import com.nuai.onboarding.ui.adapters.SpinnerAdapter
import com.nuai.profile.model.api.request.UpdateProfileRequest
import com.nuai.profile.viewmodel.ProfileViewModel
import com.nuai.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class SettingsActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, SettingsActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: SettingsActivityBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private var dob = ""
    private var genderList: ArrayList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.settings_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.settings))
        initClickListener()
    }

    private fun initClickListener() {
        binding.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.app_walk_through_text -> {
                TutorialActivity.startActivity(this, TutorialActivity.Companion.From.SETTINGS)
            }
            R.id.send_feedback_text -> {
                WebActivity.startActivity(
                    this@SettingsActivity,
                    getString(R.string.send_feedback),
                    "https://www.facebook.com"
                )
            }
            R.id.terms_of_service_text -> {
                WebActivity.startActivity(
                    this@SettingsActivity,
                    getString(R.string.terms_of_service),
                    "https://www.google.com"
                )
            }
            R.id.privacy_policy_text -> {
                WebActivity.startActivity(
                    this@SettingsActivity,
                    getString(R.string.privacy_policy),
                    "https://www.facebook.com"
                )
            }
        }
    }

}