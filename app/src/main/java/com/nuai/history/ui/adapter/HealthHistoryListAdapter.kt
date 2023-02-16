package com.nuai.history.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nuai.R
import com.nuai.databinding.ItemHealthHistoryBinding
import com.nuai.home.model.HealthHistory
import com.nuai.utils.BindingViewHolder
import com.nuai.utils.CommonUtils
import com.nuai.utils.Enums

internal class HealthHistoryListAdapter(items: ArrayList<HealthHistory>) :
    RecyclerView.Adapter<BindingViewHolder<ItemHealthHistoryBinding>>() {

    private var items: ArrayList<HealthHistory> = arrayListOf()
    internal var selectHealthHistoryListener: SelectHealthHistoryListener? = null
    private lateinit var context: Context

    init {
        this.items = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemHealthHistoryBinding> {
        context = parent.context
        return BindingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_health_history, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<ItemHealthHistoryBinding>,
        position: Int
    ) {
        val history = items[position]
        holder.binding.history = history
        holder.binding.historyListener = selectHealthHistoryListener
        if (!history.wellnessScore.isNullOrEmpty()) {
            when (history.wellnessScore) {
                Enums.WellnessScore.HIGH.toString() -> {
                    CommonUtils.setBgColor(
                        context,
                        holder.binding.crHistoryRoot.background,
                        R.color.high_score_bg_color
                    )
                    CommonUtils.setBgColor(
                        context,
                        holder.binding.ivIndicator.drawable,
                        R.color.green_text_color
                    )
                }
                Enums.WellnessScore.LOW.toString() -> {
                    CommonUtils.setBgColor(
                        context,
                        holder.binding.crHistoryRoot.background,
                        R.color.low_score_bg_color
                    )
                    CommonUtils.setBgColor(
                        context,
                        holder.binding.ivIndicator.drawable,
                        R.color.red
                    )
                }
            }
        }
        if (!history.scanType.isNullOrEmpty()) {
            when (history.scanType) {
                Enums.ScanType.FACE.toString() -> {
                    holder.binding.ivMeasureType.setImageResource(R.drawable.face_blue_circle)
                }
                Enums.ScanType.FINGER.toString() -> {
                    holder.binding.ivMeasureType.setImageResource(R.drawable.finger_blue_circle)
                }
            }
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface SelectHealthHistoryListener {
        fun onHealthHistoryClick(history: HealthHistory?)
    }

}