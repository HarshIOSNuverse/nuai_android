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
        val bgColor = when (position % 4) {
            0 -> {
                R.color.history_bg_color_0
            }
            1 -> {
                R.color.history_bg_color_1
            }
            2 -> {
                R.color.history_bg_color_2
            }
            3 -> {
                R.color.history_bg_color_3
            }
            else -> {
                R.color.history_bg_color_0
            }
        }
        CommonUtils.setBgColor(
            context,
            holder.binding.crHistoryRoot.background,
            bgColor
        )
        var wellnessScore = "${history.wellnessScore}/10 | "
        when (history.wellnessScore) {
            8, 9, 10 -> {
                CommonUtils.setBgColor(
                    context,
                    holder.binding.ivIndicator.drawable,
                    R.color.good_dot_color
                )
                wellnessScore += context.getString(R.string.high)
            }
            4, 5, 6, 7 -> {
                CommonUtils.setBgColor(
                    context,
                    holder.binding.ivIndicator.drawable,
                    R.color.medium_dot_color
                )
                wellnessScore += context.getString(R.string.medium)
            }
            else -> {
                CommonUtils.setBgColor(
                    context,
                    holder.binding.ivIndicator.drawable,
                    R.color.low_dot_color
                )
                wellnessScore += context.getString(R.string.low)
            }
        }
        holder.binding.tvWellnessScore.text = wellnessScore
        if (!history.scanBy.isNullOrEmpty()) {
            when (history.scanBy) {
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
        fun onViewMoreClick(history: HealthHistory?)
    }

}