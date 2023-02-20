package com.nuai.settings.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.nuai.BuildConfig
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.SettingsActivityBinding
import com.nuai.interfaces.DialogClickListener
import com.nuai.network.ResponseStatus
import com.nuai.network.Status
import com.nuai.network.Url
import com.nuai.onboarding.ui.activity.LoginRegisterActivity
import com.nuai.onboarding.ui.activity.TutorialActivity
import com.nuai.onboarding.ui.activity.WebActivity
import com.nuai.profile.viewmodel.ProfileViewModel
import com.nuai.utils.AlertDialogManager
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.CommonUtils
import com.nuai.utils.Pref
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.settings_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.settings))
        initClickListener()
        initObserver()
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        binding.appVersionSdkVersionText.text =
            "${getString(R.string.app_version)} ${BuildConfig.APP_VERSION} | ${getString(R.string.sdk_version)} ${ai.binah.hrv.BuildConfig.VERSION_NAME}"
    }

    private fun initObserver() {
        lifecycleScope.launch {
            profileViewModel.deleteAccountState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }
                    Status.SUCCESS -> {
                        if (it.data != null && (it.code == ResponseStatus.STATUS_CODE_SUCCESS)) {
                            Pref.logout()
                            LoginRegisterActivity.clearTopAndOpenLoginSignUpActivity(this@SettingsActivity)
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@SettingsActivity, it.message)
                    }
                }
            }
        }
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
                    Url.HOST + "terms_condition"
                )
            }
            R.id.privacy_policy_text -> {
                WebActivity.startActivity(
                    this@SettingsActivity,
                    getString(R.string.privacy_policy),
                    Url.HOST + "privacy_policy"
                )
            }
            R.id.delete_account_text -> {
                AlertDialogManager.showConfirmationDialog(
                    this, getString(R.string.app_name),
                    getString(R.string.are_you_sure_want_to_delete_account),
                    getString(R.string.delete), ContextCompat.getColor(this, R.color.white),
                    R.drawable.rc_red_filled_c25, getString(R.string.no),
                    ContextCompat.getColor(this, R.color.white),
                    R.drawable.rc_black_filled_c25, false,
                    object : DialogClickListener {
                        override fun onButton1Clicked() {
                            profileViewModel.deleteAccount()
                        }

                        override fun onButton2Clicked() {
                        }

                        override fun onCloseClicked() {

                        }
                    })
            }
        }
    }

}