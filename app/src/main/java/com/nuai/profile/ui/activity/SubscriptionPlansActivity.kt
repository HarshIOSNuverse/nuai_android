package com.nuai.profile.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.SubscriptionPlansActivityBinding
import com.nuai.home.model.Reading
import com.nuai.profile.ui.adapters.SubscriptionPlanListAdapter
import com.nuai.profile.viewmodel.ProfileViewModel
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SubscriptionPlansActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, SubscriptionPlansActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }

        fun startActivityForResult(
            activity: Activity, profileLauncher: ActivityResultLauncher<Intent>
        ) {
            Intent(activity, SubscriptionPlansActivity::class.java).run {
                profileLauncher.launch(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: SubscriptionPlansActivityBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private val subscriptionList: ArrayList<Reading> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.subscription_plans_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.subscriptions_plan))
        initClickListener()
        initAdapter()
        initObserver()
        Handler(Looper.getMainLooper()).postDelayed({
            init()
        }, 100)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init() {
        subscriptionList.clear()
        subscriptionList.add(Reading().apply {
            id = 1
            title = "Monthly"
            observedValue = "$2.95"
            shortDesc = "No Limit for Scan"
        })
        subscriptionList.add(Reading().apply {
            id = 2
            title = "Yearly"
            observedValue = "$30"
            shortDesc = "You save $6.00"
        })
        binding.adapter!!.notifyDataSetChanged()
        setNoResult()
    }

    private fun setNoResult() {
        if (subscriptionList.isNotEmpty()) {
            binding.viewFlipper.displayedChild = 1
        } else {
            binding.viewFlipper.displayedChild = 2
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
//            profileViewModel.updateProfileState.collect {
//                when (it.status) {
//                    Status.LOADING -> {
//                        showHideProgress(it.data == null)
//                    }
//                    Status.SUCCESS -> {
//                        if (it.data != null
//                            && (it.code == ResponseStatus.STATUS_CODE_SUCCESS
//                                    || it.code == ResponseStatus.STATUS_CODE_CREATED)
//                        ) {
//                            setResult(Activity.RESULT_OK)
//                            profileViewModel.getMe()
//                        }
//                    }
//                    Status.ERROR -> {
//                        showHideProgress(false)
//                        CommonUtils.showToast(this@SubscriptionPlansActivity, it.message)
//                    }
//                }
//            }
        }
    }

    private fun initClickListener() {
        binding.onClickListener = this
        initNoInternet(binding.noInternetConnection) {
            init()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.continue_btn -> {
//                profileViewModel.updateProfile(request)
                if (binding.adapter!!.selectedSubscription != null) {
                    PaymentStatusActivity.startActivity(
                        this,
                        binding.adapter!!.selectedSubscription
                    )
                } else {
                    CommonUtils.showToast(this, getString(R.string.pls_select_plan))
                }
            }
        }
    }

    private fun initAdapter() {
        binding.adapter = SubscriptionPlanListAdapter(subscriptionList).apply {
            subscriptionListener = object : SubscriptionPlanListAdapter.SubscriptionListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onSubscriptionClick(reading: Reading) {
                    selectedSubscription = reading
                    binding.adapter!!.notifyDataSetChanged()
                }
            }
        }
    }
}