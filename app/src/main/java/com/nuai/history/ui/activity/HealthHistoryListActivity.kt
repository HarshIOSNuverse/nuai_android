package com.nuai.history.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.HealthHistoryListActivityBinding
import com.nuai.history.ui.adapter.HealthHistoryListAdapter
import com.nuai.history.viewmodel.HistoryViewModel
import com.nuai.home.model.HealthBasicInfo
import com.nuai.home.ui.activity.MeasurementResultActivity
import com.nuai.network.ResponseStatus
import com.nuai.network.Status
import com.nuai.utils.*
import com.prolificinteractive.materialcalendarview.CalendarDay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class HealthHistoryListActivity : BaseActivity() {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, HealthHistoryListActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: HealthHistoryListActivityBinding
    private val historyViewModel: HistoryViewModel by viewModels()
    private var selectedDate: String? = ""
    private val historyList: ArrayList<HealthBasicInfo> = arrayListOf()
    private var screenHeight = 0
    private var bottom = 0
    private var progressOrNoDataHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.health_history_list_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.health_history))
        initClickListener()
        initObserver()
        initAdapter()
        init()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init() {
        binding.calenderView.state().edit().setMaximumDate(CalendarDay.today()).commit()
        selectedDate = DateFormatter.getDateToString(
            DateFormatter.yyyy_MM_dd_DASH,
            Calendar.getInstance().time
        )
        binding.calenderView.selectedDate = CalendarDay.today()
        updateDateView()
        Handler(Looper.getMainLooper()).postDelayed({
            getCalendarDateByMonth(binding.calenderView.selectedDate!!)
            historyViewModel.getHistoryList(selectedDate!!)
        }, 500)
    }

    private fun getCalendarDateByMonth(date: CalendarDay) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, date.year)
        calendar.set(Calendar.MONDAY, (date.month - 1))
        calendar.set(Calendar.DAY_OF_MONTH, date.day)
        val month = DateFormatter.getDateToString(DateFormatter.MMM_yyyy_DASH, calendar.time)
        historyViewModel.getCalenderDateByMonth(month)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObserver() {
        lifecycleScope.launch {
            historyViewModel.calendarDateByMonthState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(false)
                    }
                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.data != null && (it.code == ResponseStatus.STATUS_CODE_SUCCESS)) {
                            if (!it.data.info.isNullOrEmpty()) {
                                val calendar = Calendar.getInstance()
                                val low: ArrayList<CalendarDay> = arrayListOf()
                                val medium: ArrayList<CalendarDay> = arrayListOf()
                                val good: ArrayList<CalendarDay> = arrayListOf()
                                it.data.info?.forEach { calendarDate ->
                                    if (calendarDate.score != null) {
                                        val date = DateFormatter.getDateByString(
                                            DateFormatter.yyyy_MM_dd_DASH, calendarDate.date!!,
                                            TimeZone.getDefault()
                                        )
                                        if (date != null) {
                                            calendar.time = date
                                            when (calendarDate.score) {
                                                8, 9, 10 -> {
                                                    good.add(
                                                        CalendarDay.from(
                                                            calendar.get(Calendar.YEAR),
                                                            calendar.get(Calendar.MONTH) + 1,
                                                            calendar.get(Calendar.DATE),
                                                        )
                                                    )
                                                }
                                                4, 5, 6, 7 -> {
                                                    medium.add(
                                                        CalendarDay.from(
                                                            calendar.get(Calendar.YEAR),
                                                            calendar.get(Calendar.MONTH) + 1,
                                                            calendar.get(Calendar.DATE),
                                                        )
                                                    )
                                                }
                                                else -> {
                                                    low.add(
                                                        CalendarDay.from(
                                                            calendar.get(Calendar.YEAR),
                                                            calendar.get(Calendar.MONTH) + 1,
                                                            calendar.get(Calendar.DATE),
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }


                                if (medium.isNotEmpty()) {
                                    binding.calenderView.addDecorator(
                                        EventDecorator(
                                            Color.parseColor("#FFD231"),
                                            medium
                                        )
                                    )
                                }
                                if (good.isNotEmpty()) {
                                    binding.calenderView.addDecorator(
                                        EventDecorator(
                                            Color.parseColor("#43C949"),
                                            good
                                        )
                                    )
                                }
                                if (low.isNotEmpty()) {
                                    binding.calenderView.addDecorator(
                                        EventDecorator(
                                            Color.parseColor("#FF6161"),
                                            low
                                        )
                                    )
                                }
                                binding.calenderView.invalidateDecorators()
                            }
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@HealthHistoryListActivity, it.message)
                    }
                }
            }
        }
        lifecycleScope.launch {
            historyViewModel.historyListState.collect {
                if (screenHeight == 0 || progressOrNoDataHeight == 0) {
                    screenHeight = getScreenHeight()
                    bottom = (binding.crHeader.y * 1.4).toInt() + binding.crHeader.height
                    if (bottom != 0)
                        progressOrNoDataHeight = screenHeight - bottom
                }
                when (it.status) {
                    Status.LOADING -> {
//                        showHideProgress(it.data == null)
                        showHideProgress(false)
                        binding.viewFlipper.displayedChild = 0
                        binding.progressHolder.crProgressBar.minHeight = progressOrNoDataHeight
                    }
                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.data != null && (it.code == ResponseStatus.STATUS_CODE_SUCCESS)) {
                            historyList.clear()
                            if (!it.data.data?.scanResults.isNullOrEmpty()) {
                                historyList.addAll(it.data.data?.scanResults!!)
                                binding.tvFaceCount.text =
                                    historyList.count { face -> face.scanBy == Enums.ScanType.FACE.toString() }
                                        .toString()
                                binding.tvFingerCount.text =
                                    historyList.count { finger -> finger.scanBy == Enums.ScanType.FINGER.toString() }
                                        .toString()
                            } else {
                                binding.tvFaceCount.text = "0"
                                binding.tvFingerCount.text = "0"
                            }
                            binding.adapter!!.notifyDataSetChanged()
                            setNoResult()
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@HealthHistoryListActivity, it.message)
                    }
                }
            }
        }
    }

    private fun setNoResult() {
        if (historyList.isNotEmpty()) {
            binding.viewFlipper.displayedChild = 1
        } else {
            binding.viewFlipper.displayedChild = 2
            binding.tvNoDataFound.height = progressOrNoDataHeight
        }
    }

    private fun updateDateView() {
        binding.tvDate.text = if (!selectedDate.isNullOrEmpty()) DateFormatter.getFormattedDate(
            DateFormatter.yyyy_MM_dd_DASH,
            selectedDate!!,
            DateFormatter.MMMM_d_yyyy
        )
        else ""
    }

    private fun initClickListener() {
        binding.calenderView.setOnMonthChangedListener { _, date ->
            getCalendarDateByMonth(date)
        }
        binding.calenderView.setOnDateChangedListener { _, date, selected ->
            if (selected) {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, date.year)
                calendar.set(Calendar.MONDAY, (date.month - 1))
                calendar.set(Calendar.DAY_OF_MONTH, date.day)
                val tempDate =
                    DateFormatter.getDateToString(DateFormatter.yyyy_MM_dd_DASH, calendar.time)
                if (tempDate != selectedDate) {
                    selectedDate = tempDate
                    historyViewModel.getHistoryList(selectedDate!!)
                    updateDateView()
                }
            }
        }
    }

    private fun initAdapter() {
        binding.adapter = HealthHistoryListAdapter(historyList).apply {
            selectHealthHistoryListener =
                object : HealthHistoryListAdapter.SelectHealthHistoryListener {
                    override fun onViewMoreClick(history: HealthBasicInfo?) {
                        if (history != null)
                            MeasurementResultActivity.startActivityForResult(
                                this@HealthHistoryListActivity,
                                history.id,
                                detailLauncher
                            )
                    }
                }
        }
    }

    private val detailLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                historyViewModel.getHistoryList(selectedDate!!)
            }
        }
}