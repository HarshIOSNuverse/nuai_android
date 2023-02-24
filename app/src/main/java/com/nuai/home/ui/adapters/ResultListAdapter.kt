package com.nuai.home.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nuai.R
import com.nuai.databinding.ItemResultBinding
import com.nuai.home.model.Reading
import com.nuai.utils.BindingViewHolder
import com.nuai.utils.CommonUtils
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
            holder.binding.crReadingRoot.background,
            bgColor
        )
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