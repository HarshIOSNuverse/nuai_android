package com.checkmyself.profile.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.ProductType
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.SubscriptionPlansActivityBinding
import com.checkmyself.network.ResponseStatus
import com.checkmyself.network.Status
import com.checkmyself.profile.model.api.request.PurchaseRequest
import com.checkmyself.profile.ui.adapters.SubscriptionPlanListAdapter
import com.checkmyself.profile.viewmodel.ProfileViewModel
import com.checkmyself.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


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
    private val subscriptionList: ArrayList<ProductDetails> = arrayListOf()
    private var billingClient: BillingClient? = null
    private var purchase: Purchase? = null

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
//                            if (purchase != null && purchase!!.purchaseState == Purchase.PurchaseState.PURCHASED) {
//                                if (!purchase!!.isAcknowledged) {
//                                    val acknowledgePurchaseParams =
//                                        AcknowledgePurchaseParams.newBuilder()
//                                            .setPurchaseToken(purchase!!.purchaseToken)
//                                    val ackPurchaseResult = withContext(Dispatchers.IO) {
//                                        billingClient!!.acknowledgePurchase(
//                                            acknowledgePurchaseParams.build()
//                                        ) { result ->
//                                            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
//                                                Logger.d(
//                                                    "Sub",
//                                                    result.debugMessage + "Code = " + result.responseCode
//                                                )
//                                            }
//                                        }
//                                    }
//                                }
//                            }
                            PaymentStatusActivity.startActivityForResult(
                                this@SubscriptionPlansActivity,
                                it.data.id, launcher
                            )
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
                    callInAppPurchase(selectedPlan)
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
                    CommonUtils.showToast(this, billingResult.debugMessage)
                }
            }
        }

    private fun addSubscription(purchase: Purchase?) {
        if (CommonUtils.isNetworkAvailable(this)) {
            val selectedPlan = binding.adapter!!.selectedSubscription
            if (selectedPlan != null && purchase != null) {
                this.purchase = purchase
                val currencyCode = selectedPlan.subscriptionOfferDetails!![0]!!.pricingPhases.pricingPhaseList[0]!!.priceCurrencyCode
                val price =
                    CommonUtils.getPriceWithoutCurrency(
                        currencyCode,
                        selectedPlan.subscriptionOfferDetails!![0]!!.pricingPhases.pricingPhaseList[0]!!.formattedPrice
                    ).toString()

                val request = PurchaseRequest(
                    price,
                    currencyCode,
                    if (Enums.SubscriptionType.MONTHLY.toString()
                            .lowercase() == selectedPlan.name.lowercase()
                    ) Enums.SubscriptionType.MONTHLY.toString()
                    else Enums.SubscriptionType.YEARLY.toString(),
                    purchase.orderId,
                    DateFormatter.getDateTimeInUTC(Date().time, DateFormatter.yyyy_MM_dd_HH_mm_ss),
                    when (purchase.purchaseState) {
                        Purchase.PurchaseState.PURCHASED,
                        Purchase.PurchaseState.PENDING -> {
                            Enums.PurchaseStatus.SUCCESS.toString()
                        }
                        else -> {
                            Enums.PurchaseStatus.FAILED.toString()
                        }
                    },
                    selectedPlan.productId,
                    purchase.purchaseToken

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
                subscriptionList.clear()
                subscriptionList.addAll(productDetailsList)
            }
            runOnUiThread {
                binding.adapter!!.setSaveAmountForYearlyPlan(subscriptionList)
                binding.adapter!!.notifyDataSetChanged()
                setNoResult()
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
                    .setOfferToken("" + productDetails.subscriptionOfferDetails?.get(0)?.offerToken)
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
                override fun onSubscriptionClick(reading: ProductDetails) {
                    selectedSubscription = reading
                    binding.adapter!!.notifyDataSetChanged()
                }
            }
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
}