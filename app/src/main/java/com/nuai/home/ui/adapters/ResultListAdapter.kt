package com.nuai.home.ui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.nuai.R
import com.nuai.databinding.ItemResultBinding
import com.nuai.home.model.Reading
import com.nuai.utils.BindingViewHolder
import com.nuai.utils.CommonUtils
import com.nuai.utils.Enums
import com.nuai.utils.ImageSetter

internal class ResultListAdapter(items: ArrayList<Reading>) :
    RecyclerView.Adapter<BindingViewHolder<ItemResultBinding>>() {

    private var items: ArrayList<Reading> = arrayListOf()
    internal var resultListener: ResultListener? = null
    private lateinit var context: Context

    init {
        this.items = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemResultBinding> {
        context = parent.context
        return BindingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_result, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<ItemResultBinding>,
        position: Int
    ) {
        val reading = items[position]
        holder.binding.reading = reading
        ImageSetter.loadImage(
            reading.levelIcon,
            R.drawable.rc_black_border_c25,
            holder.binding.ivReadingIndicator
        )
        holder.binding.tvObservedValue.text = if (!reading.observedValue.isNullOrEmpty()) {
            if (reading.title == "Stress Level")
                CommonUtils.getStressLevel(reading.observedValue)
            else
                reading.observedValue
        } else {
            "N/A"
        }
        val bgColor = if (!reading.colorCode.isNullOrEmpty()) {
            reading.colorCode!!.toColorInt()
        } else {
            R.color.history_bg_color_0
        }

        CommonUtils.setBgColor(
            context,
            holder.binding.crReadingRoot.background,
            bgColor
        )
        if (reading.observedValue.isNullOrEmpty()) {
            holder.binding.tvTitle.text =
                String.format(context.getString(R.string.reading_not_recorded_msg), reading.title)
            holder.binding.tvShortDesc.text =
                context.getString(R.string.no_enough_data_was_recorded)
        } else {
            holder.binding.tvTitle.text = String.format(
                context.getString(R.string.reading_name_title_msg),
                reading.title,
                reading.level?.lowercase()?.replace("_", " ")
            )
            holder.binding.tvShortDesc.text = reading.shortDesc
        }
        // Hide learn more for Hemoglobin and Hemoglobin H1c
        holder.binding.tvLearnMore.visibility =
            if (!reading.title.isNullOrEmpty()
                && (reading.title == Enums.ResultType.HEMOGLOBIN.type
                        || reading.title == Enums.ResultType.HEMOGLOBIN_A1C.type)
            )
                View.GONE
            else View.VISIBLE
        holder.binding.resultListener = resultListener
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface ResultListener {
        fun onLearnMoreClick(reading: Reading)
    }

}