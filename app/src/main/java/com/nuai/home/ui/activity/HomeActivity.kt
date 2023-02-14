package com.nuai.home.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.HomeActivityBinding
import com.nuai.network.ResponseStatus
import com.nuai.network.Status
import com.nuai.onboarding.viewmodel.OnBoardingViewModel
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.run {
                activity.startActivity(this)
                activity.finish()
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: HomeActivityBinding
    private val onBoardingViewModel: OnBoardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.home_activity)
        initClickListener()
        initObserver()
        init()
    }

    private fun init() {
        enableDisableButton(
            binding.submitBtn, true
        )
    }

    private fun initObserver() {
        lifecycleScope.launch {
            onBoardingViewModel.signupState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }
                    Status.SUCCESS -> {
                        if (it.data != null
                            && (it.code == ResponseStatus.STATUS_CODE_SUCCESS
                                    || it.code == ResponseStatus.STATUS_CODE_CREATED)
                        ) {
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@HomeActivity, it.message)
                    }
                }
            }
        }
    }

    private fun initClickListener() {
        binding.onClickListener = this
    }

    override fun onClick(v: View?) {
        val errorColor = ContextCompat.getColor(this, R.color.error_msg_text_color)
        val normalColor = ContextCompat.getColor(this, R.color.et_hint_color)
        when (v!!.id) {
            R.id.ivBgImage -> {
                finish()
            }

            R.id.submit_btn -> {

            }
        }
    }
}