package com.nuai.profile.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.PaymentDetailActivityBinding
import com.nuai.network.ResponseStatus
import com.nuai.network.Status
import com.nuai.profile.model.PaymentInfo
import com.nuai.profile.viewmodel.ProfileViewModel
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.CommonUtils
import com.nuai.utils.IntentConstant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PaymentDetailActivity : BaseActivity() {
    companion object {
        fun startActivity(activity: Activity, paymentId: String?) {
            Intent(activity, PaymentDetailActivity::class.java).apply {
                putExtra(IntentConstant.ID, paymentId)
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: PaymentDetailActivityBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private var paymentId: String? = null
    private var paymentInfo: PaymentInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.payment_detail_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.payment_details))
        initClickListener()
        initObserver()
        init()
    }

    private fun init() {
        intent?.run {
            paymentId = getStringExtra(IntentConstant.ID)
        }
        getMyPlans()
    }

    private fun getMyPlans() {
        if (CommonUtils.isNetworkAvailable(this)) {
            binding.viewFlipper.displayedChild = 0
            profileViewModel.getPaymentDetail(paymentId)
        } else {
            binding.viewFlipper.displayedChild = 3
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            profileViewModel.paymentDetailState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(false)
                    }
                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.data != null && it.code == ResponseStatus.STATUS_CODE_SUCCESS) {
                            paymentInfo = it.data.data
                        }
                        setPaymentDetails()
                    }
                    Status.ERROR -> {
                        setPaymentDetails()
                        showHideProgress(false)
                        CommonUtils.showToast(this@PaymentDetailActivity, it.message)
                    }
                }
            }
        }
    }

    private fun setPaymentDetails() {
        if (paymentInfo != null) {
            binding.viewFlipper.displayedChild = 1
        } else {
            binding.viewFlipper.displayedChild = 2
        }
    }

    private fun initClickListener() {
        initNoInternet(binding.noInternetConnection) {
            getMyPlans()
        }
    }
}