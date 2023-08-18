package com.checkmyself.profile.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.PaymentDetailActivityBinding
import com.checkmyself.network.ResponseStatus
import com.checkmyself.network.Status
import com.checkmyself.profile.model.PaymentInfo
import com.checkmyself.profile.viewmodel.ProfileViewModel
import com.checkmyself.utils.AnimationsHandler
import com.checkmyself.utils.CommonUtils
import com.checkmyself.utils.DateFormatter
import com.checkmyself.utils.IntentConstant
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
                            paymentInfo = it.data.info
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

    @SuppressLint("SetTextI18n")
    private fun setPaymentDetails() {
        if (paymentInfo != null) {
            binding.viewFlipper.displayedChild = 1
            binding.tvPlanType.text =
                String.format(
                    getString(R.string.type_plan),
                    CommonUtils.getFirstLatterCap(paymentInfo?.planType)
                )
//            binding.tvPlanAmount.text =
//                CommonUtils.getCurrencySymbol(paymentInfo?.trxCurrency) + " " + CommonUtils.roundDouble(
//                    paymentInfo!!.trxAmount,
//                    2
//                )
            binding.tvPlanAmount.text =
                CommonUtils.getCurrencySymbol(paymentInfo?.trxCurrency) + " " + CommonUtils.roundDouble1(paymentInfo!!.trxAmount, 2)
            if (!paymentInfo?.trxDatetime.isNullOrEmpty())
                binding.tvPurchaseDate.text = String.format(
                    getString(R.string.purchased_on), DateFormatter.getFormattedByString(
                        DateFormatter.SERVER_DATE_FORMAT, DateFormatter.MMMM_d_yyyy_hh_mm_a,
                        paymentInfo?.trxDatetime!!
                    )
                )
            binding.tvName.text = paymentInfo?.user?.fullName
            if (!paymentInfo?.endDate.isNullOrEmpty())
                binding.tvPlanExpireDate.text = DateFormatter.getFormattedByString(
                    DateFormatter.yyyy_MM_dd_DASH, DateFormatter.MMMM_d_yyyy,
                    paymentInfo?.endDate!!
                )
            if (!paymentInfo?.trxDatetime.isNullOrEmpty())
                binding.tvTransactionDate.text = DateFormatter.getFormattedByString(
                    DateFormatter.SERVER_DATE_FORMAT, DateFormatter.MMMM_d_yyyy,
                    paymentInfo?.trxDatetime!!
                )

            binding.tvTransactionNumber.text = paymentInfo?.trxNo
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