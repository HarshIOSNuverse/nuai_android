package com.checkmyself.profile.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.MyPlansActivityBinding
import com.checkmyself.network.ResponseStatus
import com.checkmyself.network.Status
import com.checkmyself.profile.model.api.response.MyPlansResponse
import com.checkmyself.profile.viewmodel.ProfileViewModel
import com.checkmyself.utils.AnimationsHandler
import com.checkmyself.utils.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MyPlansActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, MyPlansActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: MyPlansActivityBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.my_plans_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.my_plan))
        enableDisableButton(binding.upgradeNowBtn, false)
        initClickListener()
        initObserver()
        init()
    }

    private fun init() {
        getMyPlans()
    }

    private fun getMyPlans() {
        if (CommonUtils.isNetworkAvailable(this)) {
            binding.viewFlipper.displayedChild = 0
            profileViewModel.getMyPlans()
        } else {
            binding.viewFlipper.displayedChild = 3
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            profileViewModel.getMyPlanState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(false)
                    }
                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.data != null && it.code == ResponseStatus.STATUS_CODE_SUCCESS) {
                            setPlanDetails(it.data)
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@MyPlansActivity, it.message)
                    }
                }
            }
        }
    }

    private fun setPlanDetails(data: MyPlansResponse?) {
        if (data?.activePlan != null) {
            binding.viewFlipper.displayedChild = 1
        } else {
            binding.viewFlipper.displayedChild = 2
        }
        if (data?.upcomingPlan != null) {
            enableDisableButton(binding.upgradeNowBtn, false)
            binding.crUpcomingPlan.visibility = View.VISIBLE
        } else {
            enableDisableButton(binding.upgradeNowBtn, true)
            binding.crUpcomingPlan.visibility = View.GONE
        }
    }

    private fun initClickListener() {
        binding.onClickListener = this
        initNoInternet(binding.noInternetConnection) {
            getMyPlans()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.subscribe_now_btn -> {
                SubscriptionPlansActivity.startActivityForResult(this, subscriptionLauncher)
            }
        }
    }

    private val subscriptionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                getMyPlans()
            }
        }
}