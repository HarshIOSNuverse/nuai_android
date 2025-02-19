package com.checkmyself.home.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.checkmyself.R
import com.checkmyself.databinding.ItemMeasurementHeaderBinding
import com.checkmyself.databinding.ItemResultDeleteBinding
import com.checkmyself.databinding.ItemResultWrapperBinding
import com.checkmyself.home.model.Reading
import com.checkmyself.home.model.ResultWrapper
import com.checkmyself.home.model.api.response.MeasurementResponse
import com.checkmyself.utils.BindingViewHolder
import com.checkmyself.utils.CommonUtils
import com.checkmyself.utils.Enums

internal class MeasurementScreenAdapter(items: ArrayList<Any>) :
    RecyclerView.Adapter<BindingViewHolder<ViewDataBinding>>() {

    private var items: ArrayList<Any> = arrayListOf()
    internal var measurementListener: MeasurementListener? = null
    private lateinit var context: Context

    init {
        this.items = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ViewDataBinding> {
        context = parent.context
        return when (viewType) {
            HEADER ->
                BindingViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_measurement_header, parent, false)
                )
            LIST ->
                BindingViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_result_wrapper, parent, false)
                )
            DELETE ->
                BindingViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_result_delete, parent, false)
                )

            else ->
                BindingViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_result_wrapper, parent, false)
                )
        }
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<ViewDataBinding>,
        position: Int
    ) {
        when (holder.binding) {
            is ItemMeasurementHeaderBinding -> {
                val measurementDetail = items[position] as MeasurementResponse
                val basicInfo = measurementDetail.basicInfo!!
                holder.binding.history = basicInfo
                if (!basicInfo.scanBy.isNullOrEmpty()) {
                    when (basicInfo.scanBy) {
                        Enums.ScanType.FACE.toString() -> {
                            holder.binding.ivMeasureType.setImageResource(R.drawable.face_blue_circle)
                        }
                        Enums.ScanType.FINGER.toString() -> {
                            holder.binding.ivMeasureType.setImageResource(R.drawable.finger_blue_circle)
                        }
                    }
                }
                holder.binding.tvWellnessLevel.text =
                    CommonUtils.getWellnessLevel(context, basicInfo.wellnessScore)
                holder.binding.crWellnessScoreReading.setBackgroundResource(
                    getWellnessImage(context, basicInfo.wellnessScore)
                )
                holder.binding.onClickListener = View.OnClickListener {
                    measurementListener?.onWellnessScoreClick()
                }
                val categoryList: ArrayList<String> = arrayListOf()
                if (measurementDetail.result != null) {
                    val result = measurementDetail.result!!
                    if (!result.vitalSigns.isNullOrEmpty()) {
                        categoryList.add(Enums.ResultCategory.VITAL_SIGNS.toString())
                    }
                    if (!result.blood.isNullOrEmpty()) {
                        categoryList.add(Enums.ResultCategory.BLOOD.toString())
                    }
                    if (!result.stressLevel.isNullOrEmpty()) {
                        categoryList.add(Enums.ResultCategory.STRESS_LEVEL.toString())
                    }
                    if (!result.energy.isNullOrEmpty()) {
                        categoryList.add(Enums.ResultCategory.ENERGY.toString())
                    }
                    if (!result.heartRateVariability.isNullOrEmpty()) {
                        categoryList.add(Enums.ResultCategory.HEART_RATE_VARIABILITY.toString())
                    }
                    if (!result.bloodTest.isNullOrEmpty()) {
                        categoryList.add(Enums.ResultCategory.BLOOD_TEST.toString())
                    }
                }
                holder.binding.resultCategoryAdapter =
                    ResultCategorySelectListAdapter(categoryList).apply {
                        selectCategoryListener =
                            object : ResultCategorySelectListAdapter.SelectCategoryListener {
                                @SuppressLint("NotifyDataSetChanged")
                                override fun onItemClick(categoryName: String) {
                                    holder.binding.resultCategoryAdapter!!.selectedCategory =
                                        categoryName
                                    holder.binding.resultCategoryAdapter!!.notifyDataSetChanged()
                                    val index = categoryList.indexOf(categoryName)
                                    measurementListener?.onCategoryClick(index)
                                }
                            }
                    }
            }
            is ItemResultWrapperBinding -> {
                val wrapper = items[position] as ResultWrapper
                holder.binding.tvResultCategoryName.text = when (wrapper.resultCategoryName) {
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
                        wrapper.resultCategoryName
                    }
                }
                holder.binding.adapter = ResultListAdapter(wrapper.readingList!!).apply {
                    resultListener = object : ResultListAdapter.ResultListener {
                        override fun onLearnMoreClick(reading: Reading) {
                            measurementListener?.onLearnMoreClick(reading)
                        }
                    }
                }
            }
            is ItemResultDeleteBinding -> {
                holder.binding.tvResultDelete.setOnClickListener {
                    measurementListener?.onDeleteClicked()
                }
            }
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is MeasurementResponse) {
            HEADER
        } else if (items[position] is String) {
            DELETE
        } else {
            LIST
        }
    }

    interface MeasurementListener {
        fun onWellnessScoreClick()
        fun onLearnMoreClick(reading: Reading)
        fun onCategoryClick(index: Int)
        fun onDeleteClicked()
    }

    @SuppressLint("DiscouragedApi")
    private fun getWellnessImage(context: Context, wellnessScore: Int): Int {
        val image =
            context.resources.getIdentifier(
                "wellness_score_$wellnessScore",
                "drawable",
                context.packageName
            )
        return if (image != 0) image else R.drawable.wellness_score_4
    }

    companion object {
        private const val HEADER = 1
        private const val LIST = 2
        private const val DELETE = 3
    }
}