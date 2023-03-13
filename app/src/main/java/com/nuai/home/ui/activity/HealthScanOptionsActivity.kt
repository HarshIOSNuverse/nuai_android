package com.nuai.home.ui.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.DialogScanRemainingBinding
import com.nuai.databinding.HealthScanOptionsActivityBinding
import com.nuai.network.ResponseStatus
import com.nuai.network.Status
import com.nuai.profile.ui.activity.SubscriptionPlansActivity
import com.nuai.profile.viewmodel.ProfileViewModel
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.CommonUtils
import com.nuai.utils.Pref
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HealthScanOptionsActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, HealthScanOptionsActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: HealthScanOptionsActivityBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private var user = Pref.user
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.health_scan_options_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.app_name))
        showToolbarIcon(true)
        initClickListener()
        initObserver()
        init()
    }

    private fun init() {
        updateView(binding.rgType.checkedRadioButtonId)
    }

    override fun onResume() {
        super.onResume()
        profileViewModel.getMe()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            profileViewModel.meApiState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(false)
                    }
                    Status.SUCCESS -> {
                        if (it.data != null && (it.code == ResponseStatus.STATUS_CODE_SUCCESS)) {
                            showHideProgress(false)
                            Pref.user = it.data.user
                            user = it.data.user
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@HealthScanOptionsActivity, it.message)
                    }
                }
            }
        }
    }

    private fun initClickListener() {
        binding.onClickListener = this
        binding.rgType.setOnCheckedChangeListener { _, checkedId ->
            updateView(checkedId)
        }
        enableDisableButton(binding.measureNowBtn, true)
    }

    private fun updateView(checkedId: Int) {
        if (checkedId == R.id.rb_face) {
            binding.ivTopIcon.setImageResource(R.drawable.face_option_select_top_icon)
            if (!user?.firstName.isNullOrEmpty()) {
                binding.tvTitle.text =
                    String.format(getString(R.string.face_option_select_title), user?.firstName)
            }
            binding.tvMessage.text = getString(R.string.face_option_select_msg)
        } else {
            binding.ivTopIcon.setImageResource(R.drawable.finger_option_select_top_icon)
            binding.tvTitle.text = getString(R.string.finger_option_select_title)
            binding.tvMessage.text = getString(R.string.finger_option_select_msg)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.measure_now_btn -> {
                if (user != null) {
                    if (!user!!.hasActiveSubscription && !user!!.hasUpcomingSubscription) {
                        showInformationDialog(
                            title =
                            String.format(getString(R.string.your_value_free_scans_expired), 2),
                            titleColor =
                            ContextCompat.getColor(this, R.color.error_msg_text_color),
                            msg = getString(R.string.purchase_subscription_now_for_unlimited_scans),
                            button1Message = getString(R.string.subscribe_now),
                            isContinueScan = false,
                            cancelable = true
                        )
                    } else if (user!!.hasActiveSubscription && !user!!.hasUpcomingSubscription) {
                        showInformationDialog(
                            title =
                            getString(R.string.your_plan_is_about_to_expire),
                            titleColor =
                            ContextCompat.getColor(this, R.color.error_msg_text_color),
                            msg = getString(R.string.renew_your_subscription_for_unlimited_scans),
                            button1Message = getString(R.string.renew_now),
                            isContinueScan = true,
                            cancelable = false
                        )
                    } else {
                        showInformationDialog(
                            title =
                            String.format(getString(R.string.you_have_value_free_scans_now), 2),
                            msg = getString(R.string.purchase_subscription_now_for_unlimited_scans),
                            button1Message = getString(R.string.subscribe_now),
                            isContinueScan = true,
                            cancelable = false
                        )
                    }
                }
            }
        }
    }

    private fun gotoScan() {
        if (binding.rgType.checkedRadioButtonId == R.id.rb_face) {
            ScanByFaceActivity.startActivity(this)
        } else {
            ScanByFingerActivity.startActivity(this)
        }
    }

    private fun showInformationDialog(
        title: String? = null,
        titleColor: Int = ContextCompat.getColor(this, R.color.primary_text_color),
        msg: String? = null, button1Message: String? = null,
        isContinueScan: Boolean = false, cancelable: Boolean = false
    ) {
        Dialog(this).apply {
            setCancelable(cancelable)
            val remainingBinding: DialogScanRemainingBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this@HealthScanOptionsActivity),
                R.layout.dialog_scan_remaining, null, false
            )
            setContentView(remainingBinding.root)

            if (!title.isNullOrEmpty()) {
                remainingBinding.txtTitle.text = title
                remainingBinding.txtTitle.visibility = View.VISIBLE
            } else {
                remainingBinding.txtTitle.visibility = View.GONE
            }
            remainingBinding.txtTitle.setTextColor(titleColor)

            if (!msg.isNullOrEmpty()) {
                remainingBinding.txtMessage.visibility = View.VISIBLE
                remainingBinding.txtMessage.text = msg
            } else {
                remainingBinding.txtMessage.visibility = View.GONE
                remainingBinding.txtMessage.text = ""
            }
            remainingBinding.button1.text = button1Message
            remainingBinding.button1.setOnClickListener {
                dismiss()
                SubscriptionPlansActivity.startActivity(this@HealthScanOptionsActivity)
            }
            remainingBinding.button1.visibility =
                if (!button1Message.isNullOrEmpty()) View.VISIBLE else View.GONE
            remainingBinding.txtContinueScan.visibility =
                if (isContinueScan) View.VISIBLE else View.GONE
            remainingBinding.txtContinueScan.setOnClickListener {
                dismiss()
                gotoScan()
            }
        }.run {
            show()
            window?.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }
    }
}