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
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.ProductType
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.SubscriptionPlansActivityBinding
import com.nuai.network.ResponseStatus
import com.nuai.network.Status
import com.nuai.profile.model.SubscriptionPlan
import com.nuai.profile.model.api.request.PurchaseRequest
import com.nuai.profile.ui.adapters.SubscriptionPlanListAdapter
import com.nuai.profile.viewmodel.ProfileViewModel
import com.nuai.utils.*
import com.nuai.utils.CommonUtils.TAG
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
    private val subscriptionList: ArrayList<SubscriptionPlan> = arrayListOf()
    private var billingClient: BillingClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.subscription_plans_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.subscriptions_plan))
        initClickListener()
        initBilling()
        initAdapter()
        initObserver()
        Handler(Looper.getMainLooper()).postDelayed({
            init()
        }, 100)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init() {
        subscriptionList.clear()
        subscriptionList.add(SubscriptionPlan().apply {
            id = AppConstant.MONTHLY_PLAN_ID
            title = "Monthly"
            observedValue = "$2.95"
            shortDesc = getString(R.string.no_limit_for_scan)
        })
        subscriptionList.add(SubscriptionPlan().apply {
            id = AppConstant.YEARLY_PLAN_ID
            title = "Yearly"
            observedValue = "$30"
            shortDesc = getString(R.string.you_save_doller_6)
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
            profileViewModel.subscriptionState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }
                    Status.SUCCESS -> {
                        if (it.data != null && it.code == ResponseStatus.STATUS_CODE_SUCCESS) {
                            CommonUtils.showToast(this@SubscriptionPlansActivity, it.data.message)
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@SubscriptionPlansActivity, it.message)
                    }
                }
            }
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
                val selectedPlan = binding.adapter!!.selectedSubscription
                if (selectedPlan == null) {
                    CommonUtils.showToast(this, getString(R.string.pls_select_plan))
                }/* else if (selectedPlan.skuDetailsResult == null) {
                    CommonUtils.showToast(this, getString(R.string.please_try_after_some_time))
                }*/ else if (!CommonUtils.isNetworkAvailable(this)) {
                    CommonUtils.showToast(this, getString(R.string.no_internet_connection))
                } else {
                    PaymentStatusActivity.startActivity(
                        this,
                        binding.adapter!!.selectedSubscription
                    )
                    callInAppPurchase(selectedPlan.skuDetailsResult)
                }
            }
        }
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            Logger.d(TAG, "onPurchasesUpdated() response: ${billingResult.responseCode}")
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    if (!purchases.isNullOrEmpty()) {
                        runOnUiThread {
                            addSubscription(purchases[0])
                        }
                    }
                }
                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    CommonUtils.showToast(this, "Payment cancelled")
                }
                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                    CommonUtils.showToast(this, "Already Owned")
                }
                else -> {
                    CommonUtils.showToast(this, "Something went wrong")
                }
            }
        }

    private fun addSubscription(purchase: Purchase?) {
        if (CommonUtils.isNetworkAvailable(this)) {
            val selectedPlan = binding.adapter!!.selectedSubscription
            if (selectedPlan != null) {
                val request = PurchaseRequest(
                    selectedPlan.id,
                    selectedPlan.skuDetailsResult?.oneTimePurchaseOfferDetails?.formattedPrice,
                    selectedPlan.skuDetailsResult?.oneTimePurchaseOfferDetails?.priceCurrencyCode,
                    purchase?.originalJson, purchase?.orderId,
                    purchase?.packageName, purchase?.purchaseToken
                )
                profileViewModel.addSubscription(request)
            }
        } else {
            CommonUtils.showToast(this, getString(R.string.no_internet_connection))
        }
    }

    private fun initBilling() {
        billingClient =
            BillingClient.newBuilder(this).enablePendingPurchases()
                .setListener(purchasesUpdatedListener).build()
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Logger.e(
                    "onBillingSetupFinished",
                    "onBillingSetupFinished " + billingResult.debugMessage + " " + billingResult.responseCode
                )
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    querySkuDetailsAsync()
                }
            }

            override fun onBillingServiceDisconnected() {
                Logger.e("onBilling Disconnected", "onBillingServiceDisconnected")
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun querySkuDetailsAsync() {
        val params = QueryProductDetailsParams.newBuilder().setProductList(
            listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(AppConstant.MONTHLY_PLAN_ID)
                    .setProductType(ProductType.SUBS)
                    .build(),
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(AppConstant.YEARLY_PLAN_ID)
                    .setProductType(ProductType.SUBS)
                    .build()
            )
        ).build()
        billingClient!!.queryProductDetailsAsync(params) { _, productDetailsList ->
            if (productDetailsList.isNotEmpty()) {
                for (j in productDetailsList.indices) {
                    val skuDetails = productDetailsList[j]
                    subscriptionList[j].skuDetailsResult = skuDetails
                }
                runOnUiThread {
                    binding.adapter!!.notifyDataSetChanged()
                    setNoResult()
                }
            }
        }
    }

    private fun callInAppPurchase(productDetails: ProductDetails?) {
        if (productDetails != null) {
            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                    .setProductDetails(productDetails)
                    // to get an offer token, call ProductDetails.subscriptionOfferDetails()
                    // for a list of offers that are available to the user
//                    .setOfferToken(selectedOfferToken)
                    .build()
            )

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
            val billingResult = billingClient?.launchBillingFlow(this, billingFlowParams)
            Logger.e("responseCode", billingResult.toString())
        }
    }

    private fun initAdapter() {
        binding.adapter = SubscriptionPlanListAdapter(subscriptionList).apply {
            subscriptionListener = object : SubscriptionPlanListAdapter.SubscriptionListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onSubscriptionClick(reading: SubscriptionPlan) {
                    selectedSubscription = reading
                    binding.adapter!!.notifyDataSetChanged()
                }
            }
        }
    }
}