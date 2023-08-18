package com.checkmyself.profile.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.PaymentStatusActivityBinding
import com.checkmyself.home.ui.activity.HomeActivity
import com.checkmyself.network.ResponseStatus
import com.checkmyself.network.Status
import com.checkmyself.profile.model.PaymentInfo
import com.checkmyself.profile.viewmodel.ProfileViewModel
import com.checkmyself.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PaymentStatusActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivityForResult(
            activity: Activity,
            paymentId: String?,
            launcher: ActivityResultLauncher<Intent>
        ) {
            Intent(activity, PaymentStatusActivity::class.java).apply {
                putExtra(IntentConstant.ID, paymentId)
            }.run {
                launcher.launch(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: PaymentStatusActivityBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private var paymentId: String? = null
    private var paymentInfo: PaymentInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.payment_status_activity)
        setUpToolNewBar(binding.toolbarLayout)
        hideBackButton()
        setToolBarTitle(getString(R.string.payment_status))
        initClickListener()
        initObserver()
        init()
    }

    @Suppress("DEPRECATION")
    private fun init() {
        intent?.run {
            paymentId = getStringExtra(IntentConstant.ID)
        }
        getMyPlans()
    }

    private fun getMyPlans() {
        if (CommonUtils.isNetworkAvailable(this)) {
            profileViewModel.getPaymentDetail(paymentId)
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
                        CommonUtils.showToast(this@PaymentStatusActivity, it.message)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setPaymentDetails() {
        if (paymentInfo != null) {
            if (paymentInfo!!.trxStatus == Enums.PurchaseStatus.SUCCESS.toString()) {
                binding.ivPaymentStatus.setImageResource(R.drawable.payment_status_success)
                binding.tvPaymentStatusTitle.text = getString(R.string.payment_status_success_title)
                binding.tvPaymentStatusMsg.text = getString(R.string.payment_status_success_msg)
                binding.crSuccessDetail.visibility = View.VISIBLE
                binding.measureNowBtn.text = getString(R.string.measure_now)
            } else {
                binding.ivPaymentStatus.setImageResource(R.drawable.payment_status_fail)
                binding.tvPaymentStatusTitle.text = getString(R.string.payment_status_fail_title)
                binding.tvPaymentStatusMsg.text = getString(R.string.payment_status_fail_msg)
                binding.crSuccessDetail.visibility = View.GONE
                binding.measureNowBtn.text = getString(R.string.retry_payment)
            }
//            binding.tvAmount.text =
//                CommonUtils.getCurrencySymbol(paymentInfo?.trxCurrency) + " " + CommonUtils.roundDouble(
//                    paymentInfo!!.trxAmount,
//                    2
//                )

            binding.tvAmount.text =
                CommonUtils.getCurrencySymbol(paymentInfo?.trxCurrency) + " " + CommonUtils.roundDouble1(
                    paymentInfo!!.trxAmount,
                    2
                )

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
        }
    }

    private fun initClickListener() {
        binding.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.measure_now_btn -> {
                goto()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            goto()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        goto()
    }

    private fun goto() {
        if (paymentInfo != null) {
            if (paymentInfo!!.trxStatus == Enums.PurchaseStatus.SUCCESS.toString()) {
                HomeActivity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    this,
                    AnimationsHandler.Animations.LeftToRight
                )
            } else {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}