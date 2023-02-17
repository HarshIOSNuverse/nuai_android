package com.nuai.history.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.HealthHistoryListActivityBinding
import com.nuai.history.ui.adapter.HealthHistoryListAdapter
import com.nuai.home.model.HealthHistory
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.DateFormatter
import com.nuai.utils.Enums
import com.nuai.utils.EventDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.HashMap


@AndroidEntryPoint
class HealthHistoryListActivity : BaseActivity(), View.OnClickListener {
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
    private var selectedDate: String? = ""
    private val historyList: ArrayList<HealthHistory> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.health_history_list_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.app_name))
        showToolbarIcon(true)
        initClickListener()
        initAdapter()
        init()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init() {
        selectedDate = DateFormatter.getDateToString(
            DateFormatter.yyyy_MM_dd_DASH,
            Calendar.getInstance().time
        )
        updateDateView()
        historyList.add(HealthHistory().apply {
            id = 1
            scanType = Enums.ScanType.FACE.toString()
            wellnessScore = Enums.WellnessScore.LOW.toString()
            score = "3/10 | Low"
            time = "3:30 PM"
            heartRate = 58
            breathingRate = 10
            prq = 2
            oxygenSaturation = 96
            bloodPressure = "96/60"
            stressLevel = "Normal"
            recoveryAbility = "High"
            stressResponse = "N.A."
            hrvSdnn = 48

        })
        historyList.add(HealthHistory().apply {
            id = 2
            scanType = Enums.ScanType.FINGER.toString()
            wellnessScore = Enums.WellnessScore.HIGH.toString()
            score = "8/10 | High"
            time = "12:30 PM"
            heartRate = 58
            breathingRate = 10
            prq = 2
            oxygenSaturation = 96
            bloodPressure = "96/60"
            stressLevel = "Normal"
            recoveryAbility = "High"
            stressResponse = "N.A."
            hrvSdnn = 48
        })
        historyList.add(HealthHistory().apply {
            id = 3
            scanType = Enums.ScanType.FINGER.toString()
            wellnessScore = Enums.WellnessScore.HIGH.toString()
            score = "8/10 | High"
            time = "12:30 PM"
            heartRate = 58
            breathingRate = 10
            prq = 2
            oxygenSaturation = 96
            bloodPressure = "96/60"
            stressLevel = "Normal"
            recoveryAbility = "High"
            stressResponse = "N.A."
            hrvSdnn = 48
        })
        Handler(Looper.getMainLooper()).postDelayed({
            binding.tvFaceCount.text =
                historyList.count { it.scanType == Enums.ScanType.FACE.toString() }.toString()
            binding.tvFingerCount.text =
                historyList.count { it.scanType == Enums.ScanType.FINGER.toString() }.toString()
            binding.adapter!!.notifyDataSetChanged()
            setNoResult()
        }, 1000)
    }

    private fun setNoResult() {
        if (historyList.isNotEmpty()) {
            binding.viewFlipper.displayedChild = 1
        } else {
            binding.viewFlipper.displayedChild = 2
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
        binding.onClickListener = this
        val hashMap: HashMap<CalendarDay, Int> = hashMapOf()
        hashMap[CalendarDay.today()] = R.color.red
        hashMap[CalendarDay.from(2023, 2, 25)] = R.color.green_text_color
        binding.calenderView.addDecorator(
            EventDecorator(
                Color.RED,
                arrayListOf(CalendarDay.today(), CalendarDay.from(2023, 2, 25))
            )
        )
        binding.calenderView.addDecorator(
            EventDecorator(
                ContextCompat.getColor(this, R.color.green_text_color),
                arrayListOf(CalendarDay.from(2023, 2, 18))
            )
        )
        binding.calenderView.invalidateDecorators()
        binding.calenderView.setOnDateChangedListener { _, date, selected ->
            if (selected) {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, date.year)
                calendar.set(Calendar.MONDAY, (date.month - 1))
                calendar.set(Calendar.DAY_OF_MONTH, date.day)
                selectedDate =
                    DateFormatter.getDateToString(DateFormatter.yyyy_MM_dd_DASH, calendar.time)
                updateDateView()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.measure_now_btn -> {
            }
        }
    }

    private fun initAdapter() {
        binding.adapter = HealthHistoryListAdapter(historyList).apply {
            selectHealthHistoryListener =
                object : HealthHistoryListAdapter.SelectHealthHistoryListener {
                    override fun onHealthHistoryClick(history: HealthHistory?) {

                    }
                }
        }
    }
}