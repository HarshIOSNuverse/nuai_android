package com.nuai.home.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nuai.R
import com.nuai.databinding.ItemResultCategorySelectorBinding
import com.nuai.utils.BindingViewHolder
import com.nuai.utils.Enums

internal class ResultCategorySelectListAdapter(items: ArrayList<String>) :
    RecyclerView.Adapter<BindingViewHolder<ItemResultCategorySelectorBinding>>() {

    private var items: ArrayList<String> = arrayListOf()
    internal var selectCategoryListener: SelectCategoryListener? = null
    internal var selectedCategory: String? = null
    private lateinit var context: Context

    init {
        this.items = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemResultCategorySelectorBinding> {
        context = parent.context
        return BindingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_result_category_selector, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<ItemResultCategorySelectorBinding>,
        position: Int
    ) {
        val nameEnum = items[position]
        holder.binding.tvResultName.text = when (nameEnum) {
            Enums.ResultCategory.VITAL_SIGNS.toString() -> {
                context.getString(R.string.vital_signs)
            }
            Enums.ResultCategory.BLOOD.toString() -> {
                context.getString(R.string.blood)
            }
            Enums.ResultCategory.STRESS_LEVEL.toString() -> {
                context.getString(R.string.stress_level)
            }
            Enums.ResultCategory.ENERGY.toString() -> {
                context.getString(R.string.energy)
            }
            Enums.ResultCategory.HEART_RATE_VARIABILITY.toString() -> {
                context.getString(R.string.heart_rate_variability)
            }
            Enums.ResultCategory.BLOOD_TEST.toString() -> {
                context.getString(R.string.blood_test)
            }
            else -> {
                nameEnum
            }
        }
        if (!selectedCategory.isNullOrEmpty() && selectedCategory == nameEnum) {
            holder.binding.tvResultName.setBackgroundResource(R.drawable.rc_black_filled_c25)
            holder.binding.tvResultName.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.binding.tvResultName.setBackgroundResource(R.drawable.rc_black_border_c25)
            holder.binding.tvResultName.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.primary_text_color
                )
            )
        }
        holder.binding.nameEnum = nameEnum
        holder.binding.selectCategoryListener = selectCategoryListener
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface SelectCategoryListener {
        fun onItemClick(categoryName: String)
    }

}