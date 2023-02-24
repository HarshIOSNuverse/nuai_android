package com.nuai.home.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.MeasurementResultActivityBinding
import com.nuai.history.viewmodel.HistoryViewModel
import com.nuai.home.model.Reading
import com.nuai.home.model.ResultWrapper
import com.nuai.home.model.api.response.MeasurementResponse
import com.nuai.home.ui.adapters.MeasurementScreenAdapter
import com.nuai.network.ResponseStatus
import com.nuai.network.Status
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.Enums
import com.nuai.utils.IntentConstant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MeasurementResultActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity, id: Long) {
            Intent(activity, MeasurementResultActivity::class.java).apply {
                putExtra(IntentConstant.ID, id)
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: MeasurementResultActivityBinding
    private val historyViewModel: HistoryViewModel by viewModels()
    private val screenList: ArrayList<Any> = arrayListOf()
    private var scanId: Long = 0
    private var measurementDetail: MeasurementResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.measurement_result_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.measurement_results))
        initClickListener()
        initAdapter()
        initObserver()
        init()
    }

    private fun init() {
        intent?.run {
            scanId = getLongExtra(IntentConstant.ID, 0)
        }
        historyViewModel.getScanInfo(scanId)
    }

    private fun initObserver() {
        lifecycleScope.launch {
            historyViewModel.getScanInfoState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }
                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.data != null && (it.code == ResponseStatus.STATUS_CODE_SUCCESS)) {
                            measurementDetail = it.data
                            setDetails()
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                    }
                }
            }
        }
    }

    private fun setDetails() {
        if (measurementDetail != null) {
            if (measurementDetail?.basicInfo != null) {
                screenList.add(measurementDetail?.basicInfo!!)
            }
            if (measurementDetail?.result != null) {
                val result = measurementDetail!!.result!!
                if (!result.vitalSigns.isNullOrEmpty()) {
                    screenList.add(ResultWrapper().apply {
                        resultCategoryName = Enums.ResultCategory.VITAL_SIGNS.toString()
                        readingList = result.vitalSigns
                    })
                }
                if (!result.blood.isNullOrEmpty()) {
                    screenList.add(ResultWrapper().apply {
                        resultCategoryName = Enums.ResultCategory.BLOOD.toString()
                        readingList = result.blood
                    })
                }
                if (!result.stressLevel.isNullOrEmpty()) {
                    screenList.add(ResultWrapper().apply {
                        resultCategoryName = Enums.ResultCategory.STRESS_LEVEL.toString()
                        readingList = result.stressLevel
                    })
                }
                if (!result.energy.isNullOrEmpty()) {
                    screenList.add(ResultWrapper().apply {
                        resultCategoryName = Enums.ResultCategory.ENERGY.toString()
                        readingList = result.energy
                    })
                }
                if (!result.heartRateVariability.isNullOrEmpty()) {
                    screenList.add(ResultWrapper().apply {
                        resultCategoryName = Enums.ResultCategory.HEART_RATE_VARIABILITY.toString()
                        readingList = result.heartRateVariability
                    })
                }
//                screenList.add(measurementDetail?.result!!)
            }
            binding.adapter!!.notifyDataSetChanged()
        }
    }

    private fun initClickListener() {
        binding.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

        }
    }

    private fun initAdapter() {
        binding.adapter =
            MeasurementScreenAdapter(screenList).apply {
                measurementListener =
                    object : MeasurementScreenAdapter.MeasurementListener {
                        override fun onLearnMoreClick(reading: Reading) {
                        }

                        override fun onCategoryClick(index: Int) {
                            binding.recyclerView.scrollToPosition(index + 1)
                        }
                    }
            }
    }
}