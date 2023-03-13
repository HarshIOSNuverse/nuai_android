package com.nuai.profile.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nuai.R
import com.nuai.databinding.ItemSubscriptionPlanBinding
import com.nuai.home.model.Reading
import com.nuai.utils.BindingViewHolder

internal class SubscriptionPlanListAdapter(items: ArrayList<Reading>) :
    RecyclerView.Adapter<BindingViewHolder<ItemSubscriptionPlanBinding>>() {

    private var items: ArrayList<Reading> = arrayListOf()
    internal var subscriptionListener: SubscriptionListener? = null
    var selectedSubscription: Reading? = null
    private lateinit var context: Context

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
        if (selectedSubscription != null && selectedSubscription!!.id == subscription.id) {
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

    interface SubscriptionListener {
        fun onSubscriptionClick(reading: Reading)
    }

}