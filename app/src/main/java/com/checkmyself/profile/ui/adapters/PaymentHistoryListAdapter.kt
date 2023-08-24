package com.checkmyself.profile.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.checkmyself.R
import com.checkmyself.databinding.ItemPaymentHistoryBinding
import com.checkmyself.profile.model.MyPlan
import com.checkmyself.utils.BindingViewHolder
import com.checkmyself.utils.CommonUtils
import com.checkmyself.utils.DateFormatter

internal class PaymentHistoryListAdapter(items: ArrayList<MyPlan>) :
    RecyclerView.Adapter<BindingViewHolder<ItemPaymentHistoryBinding>>() {

    private var items: ArrayList<MyPlan> = arrayListOf()
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: BindingViewHolder<ItemPaymentHistoryBinding>,
        position: Int
    ) {
        val payment = items[position]
        holder.binding.payment = payment
        holder.binding.tvUpcomingPlanType.text =
            String.format(
                context.getString(R.string.type_plan),
                CommonUtils.getFirstLatterCap(payment.planType)
            )
//        holder.binding.tvAmount.text =
//            CommonUtils.getCurrencySymbol(payment.trxCurrency) + " " + CommonUtils.roundDouble(
//                payment.trxAmount,
//                2
//            )

//        holder.binding.tvAmount.text =
//            CommonUtils.getCurrencySymbol(payment.trxCurrency) + " " + CommonUtils.roundDouble1(payment.trxAmount,2)


        if (!payment.planAmount.isNullOrEmpty()) {
            holder.binding.tvAmount.text = payment.planAmount
        } else {
            holder.binding.tvAmount.text = "-"
        }

        if (!payment.trxDatetime.isNullOrEmpty())
            holder.binding.tvPurchaseDate.text = String.format(
                context.getString(R.string.purchased_on), DateFormatter.getFormattedByString(
                    DateFormatter.SERVER_DATE_FORMAT, DateFormatter.MMMM_d_yyyy_hh_mm_a,
                    payment.trxDatetime!!
                )
            )
        holder.binding.tvTransactionStatus.text =
            CommonUtils.getFirstLatterCap(payment.trxStatus)
        holder.binding.paymentListener = paymentHistoryListener
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface PaymentHistoryListener {
        fun onViewDetailClick(payment: MyPlan?)
    }

}