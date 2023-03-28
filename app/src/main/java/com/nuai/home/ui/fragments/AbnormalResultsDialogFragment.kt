package com.nuai.home.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nuai.R
import com.nuai.databinding.AbnormalResultsBottomSheetBinding
import com.nuai.home.model.Reading
import com.nuai.home.model.ResultWrapper
import com.nuai.home.ui.adapters.MeasurementScreenAdapter
import com.nuai.utils.Enums
import dagger.hilt.android.AndroidEntryPoint
import com.nuai.home.model.api.response.Result
import com.nuai.home.ui.activity.LearnMoreActivity

@AndroidEntryPoint
class AbnormalResultsDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: AbnormalResultsBottomSheetBinding
    private val abnormalResultList: ArrayList<Any> = arrayListOf()
    private var result: Result? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.abnormal_results_bottom_sheet,
                container,
                false
            )
        initAdapter()
        init()
        return binding.root
    }

    private fun init() {
        setAbnormalResults()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAbnormalResults() {
        if (result != null) {
            if (!result!!.vitalSigns.isNullOrEmpty()) {
                abnormalResultList.add(ResultWrapper().apply {
                    resultCategoryName = Enums.ResultCategory.VITAL_SIGNS.toString()
                    readingList = result!!.vitalSigns
                })
            }
            if (!result!!.blood.isNullOrEmpty()) {
                abnormalResultList.add(ResultWrapper().apply {
                    resultCategoryName = Enums.ResultCategory.BLOOD.toString()
                    readingList = result!!.blood
                })
            }
            if (!result!!.stressLevel.isNullOrEmpty()) {
                abnormalResultList.add(ResultWrapper().apply {
                    resultCategoryName = Enums.ResultCategory.STRESS_LEVEL.toString()
                    readingList = result!!.stressLevel
                })
            }
            if (!result!!.energy.isNullOrEmpty()) {
                abnormalResultList.add(ResultWrapper().apply {
                    resultCategoryName = Enums.ResultCategory.ENERGY.toString()
                    readingList = result!!.energy
                })
            }
            if (!result!!.heartRateVariability.isNullOrEmpty()) {
                abnormalResultList.add(ResultWrapper().apply {
                    resultCategoryName = Enums.ResultCategory.HEART_RATE_VARIABILITY.toString()
                    readingList = result!!.heartRateVariability
                })
            }
            if (!result!!.bloodTest.isNullOrEmpty()) {
                abnormalResultList.add(ResultWrapper().apply {
                    resultCategoryName = Enums.ResultCategory.BLOOD_TEST.toString()
                    readingList = result!!.bloodTest
                })
            }
        }
        binding.adapter!!.notifyDataSetChanged()
    }

    private fun initAdapter() {
        binding.adapter = MeasurementScreenAdapter(abnormalResultList).apply {
            measurementListener =
                object : MeasurementScreenAdapter.MeasurementListener {
                    override fun onWellnessScoreClick() {}

                    override fun onLearnMoreClick(reading: Reading) {
                        LearnMoreActivity.startActivity(
                            this@AbnormalResultsDialogFragment.requireActivity(), reading
                        )
                    }

                    override fun onCategoryClick(index: Int) {
                        binding.recyclerView.scrollToPosition(index + 1)
                    }

                    override fun onDeleteClicked() {}
                }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val bottomSheet =
                    findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                BottomSheetBehavior.from(bottomSheet).apply {

                    setState(BottomSheetBehavior.STATE_EXPANDED)
                }
            }
        }
    }

    companion object {

        val TAG: String = AbnormalResultsDialogFragment::class.java.simpleName
        fun onNewInstance(
            result: Result?, cancellable: Boolean
        ): AbnormalResultsDialogFragment {
            return AbnormalResultsDialogFragment().apply {
                this.result = result
                isCancelable = cancellable
            }
        }
    }
}
