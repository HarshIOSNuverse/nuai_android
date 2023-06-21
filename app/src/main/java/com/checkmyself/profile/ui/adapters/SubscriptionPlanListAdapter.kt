package com.checkmyself.profile.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.ProductDetails
import com.checkmyself.R
import com.checkmyself.databinding.ItemSubscriptionPlanBinding
import com.checkmyself.utils.BindingViewHolder
import com.checkmyself.utils.CommonUtils
import com.checkmyself.utils.Enums

internal class SubscriptionPlanListAdapter(items: ArrayList<ProductDetails>) :
    RecyclerView.Adapter<BindingViewHolder<ItemSubscriptionPlanBinding>>() {

    private var items: ArrayList<ProductDetails> = arrayListOf()
    internal var subscriptionListener: SubscriptionListener? = null
    var selectedSubscription: ProductDetails? = null
    private lateinit var context: Context
    private var saving: Double = 0.0

    init {
        this.items = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BindingViewHolder<ItemSubscriptionPlanBinding> {
        context = parent.context
        return BindingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_subscription_plan, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<ItemSubscriptionPlanBinding>, position: Int
    ) {
        val subscription = items[position]
        holder.binding.subscription = subscription
        val priceWithCurrency = CommonUtils.getPriceWithCurrency(subscription)

        holder.binding.tvPrice.text = priceWithCurrency
        if (Enums.SubscriptionType.MONTHLY.toString()
                .lowercase() == subscription.name.lowercase()
        ) {
            holder.binding.tvSubscriptionMsg.text = context.getString(R.string.no_limit_for_scan)
        }/* else if (saving > 0 && Enums.SubscriptionType.YEARLY.toString()
                .lowercase() == subscription.name.lowercase()
        ) {
            val msg = CommonUtils.getCurrencySymbol(
                subscription.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
                    0
                )?.priceCurrencyCode
            ) + " " + CommonUtils.roundDouble(saving, 2)
            holder.binding.tvSubscriptionMsg.text =
                String.format(context.getString(R.string.you_save_value), msg)
        }*/ else {
            holder.binding.tvSubscriptionMsg.text = context.getString(R.string.no_limit_for_scan)
        }
        if (selectedSubscription != null && selectedSubscription!!.productId == subscription.productId) {
            holder.binding.ivRadio.setImageResource(R.drawable.radio_selected)
            holder.binding.crSubscriptionRoot.setBackgroundResource(R.drawable.subscription_row_selected)
        } else {
            holder.binding.ivRadio.setImageResource(R.drawable.radio_default)
            holder.binding.crSubscriptionRoot.setBackgroundResource(R.drawable.subscription_row_default)
        }
        holder.binding.subscriptionListener = subscriptionListener
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setSaveAmountForYearlyPlan(items: ArrayList<ProductDetails>) {
        if (items.isNotEmpty()) {
            val monthly = items.find {
                (Enums.SubscriptionType.MONTHLY.toString()
                    .lowercase() == it.name.lowercase()
                        )
            }
            val yearly = items.find {
                (Enums.SubscriptionType.YEARLY.toString()
                    .lowercase() == it.name.lowercase()
                        )
            }
            if (monthly != null && yearly != null) {
                val currencyCode =
                    monthly.subscriptionOfferDetails!![0]!!.pricingPhases.pricingPhaseList[0]!!.priceCurrencyCode
                val monthlyPrice =
                    CommonUtils.getPriceWithoutCurrency(
                        currencyCode,
                        (monthly.subscriptionOfferDetails!![0]!!.pricingPhases.pricingPhaseList[0]!!.formattedPrice)
                    )
                val yearlyPrice =
                    CommonUtils.getPriceWithoutCurrency(
                        currencyCode,
                        (yearly.subscriptionOfferDetails!![0]!!.pricingPhases.pricingPhaseList[0]!!.formattedPrice)
                    )
                if ((monthlyPrice * 12) > yearlyPrice) {
                    saving = (monthlyPrice * 12) - yearlyPrice
                }
            }
        }
    }

    interface SubscriptionListener {
        fun onSubscriptionClick(reading: ProductDetails)
    }

}