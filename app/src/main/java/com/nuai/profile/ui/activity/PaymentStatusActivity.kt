package com.nuai.profile.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.PaymentStatusActivityBinding
import com.nuai.home.ui.activity.HomeActivity
import com.nuai.profile.model.SubscriptionPlan
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.IntentConstant
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PaymentStatusActivity : BaseActivity(), View.OnClickListener {
    companion object {
        private var count = 0
        fun startActivity(activity: Activity, selectedSubscription: SubscriptionPlan?) {
            Intent(activity, PaymentStatusActivity::class.java).apply {
                putExtra(IntentConstant.READING, selectedSubscription)
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: PaymentStatusActivityBinding
    private var subscription: SubscriptionPlan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.payment_status_activity)
        setUpToolNewBar(binding.toolbarLayout)
        hideBackButton()
        setToolBarTitle(getString(R.string.payment_status))
        initClickListener()
        init()
    }

    @Suppress("DEPRECATION")
    private fun init() {
        intent?.run {
            subscription = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getParcelableExtra(IntentConstant.READING, SubscriptionPlan::class.java)
            } else {
                getParcelableExtra(IntentConstant.READING)
            }
        }
        setDetails()
    }

    private fun setDetails() {
        binding.tvAmount.text = subscription!!.observedValue
        if (count % 2 == 0) {
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
        if (count % 2 == 0) {
            HomeActivity.startActivity(this)
            AnimationsHandler.playActivityAnimation(this, AnimationsHandler.Animations.LeftToRight)
        } else {
            finish()
        }
        count++
    }
}