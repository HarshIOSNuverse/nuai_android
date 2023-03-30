package com.checkmyself.profile.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.checkmyself.R
import com.checkmyself.databinding.ItemPaymentHistoryBinding
import com.checkmyself.profile.model.PaymentInfo
import com.checkmyself.utils.BindingViewHolder

internal class PaymentHistoryListAdapter(items: ArrayList<PaymentInfo>) :
    RecyclerView.Adapter<BindingViewHolder<ItemPaymentHistoryBinding>>() {

    private var items: ArrayList<PaymentInfo> = arrayListOf()
    internal var paymentHistoryListener: PaymentHistoryListener? = null
    private lateinit var context: Context

    init {
        this.items = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemPaymentHistoryBinding> {
        context = parent.context
        return BindingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_payment_history, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<ItemPaymentHistoryBinding>,
        position: Int
    ) {
        val paymentInfo = items[position]
        holder.binding.payment = paymentInfo
        holder.binding.paymentListener = paymentHistoryListener
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface PaymentHistoryListener {
        fun onViewDetailClick(payment: PaymentInfo?)
    }

}