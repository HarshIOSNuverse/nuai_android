package com.nuai.home.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.LearnMoreActivityBinding
import com.nuai.home.model.Reading
import com.nuai.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LearnMoreActivity : BaseActivity() {
    companion object {
        fun startActivity(activity: Activity, reading: Reading) {
            Intent(activity, LearnMoreActivity::class.java).apply {
                putExtra(IntentConstant.READING, reading)
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: LearnMoreActivityBinding
    private var reading: Reading? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.learn_more_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.app_name))
        showToolbarIcon(true)
        init()
    }

    private fun init() {
        intent?.run {
            @Suppress("DEPRECATION")
            reading = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                getParcelableExtra(IntentConstant.READING, Reading::class.java)
            else
                getParcelableExtra(IntentConstant.READING)
            setReadingDetails()
        }
    }

    private fun setReadingDetails() {
        CommonUtils.setBgColor(this, binding.crReadingRoot.background, R.color.profile_top_bg_color)
        if (reading != null) {
            binding.reading = reading
            ImageSetter.loadImage(
                reading?.levelIcon,
                R.drawable.rc_black_border_c25,
                binding.ivReadingIndicator
            )

            // This logic is implemented as per client (We suggested that make Enums for that)
            if (!reading?.title.isNullOrEmpty()) {
                binding.tvTitle1.text = if (reading?.observedValue.isNullOrEmpty())
                    String.format(getString(R.string.reading_not_recorded_msg), reading?.title)
                else String.format(
                    getString(R.string.reading_name_title_msg),
                    reading?.title,
                    reading?.level?.lowercase()?.replace("_", " ")
                )
                binding.tvMsg3.visibility = View.GONE
                binding.ivImage2.visibility = View.GONE
                when (reading!!.title) {
                    Enums.ResultType.HEART_RATE.type -> {
                        binding.tvMsg1.text = getString(R.string.heart_rate_msg_1)
                        binding.ivImage1.setImageResource(R.drawable.heart_rate_img_1)
                        binding.tvMsg2.text = getString(R.string.heart_rate_msg_2)
                    }
                    Enums.ResultType.HRV_SDNN.type -> {
                        binding.tvMsg1.text = getString(R.string.hrv_sdnn_msg_1)
                        binding.ivImage1.setImageResource(R.drawable.hrv_sdnn_img_1)
                        binding.tvMsg2.text = getString(R.string.hrv_sdnn_msg_2)
                    }
                    Enums.ResultType.STRESS_LEVEL.type -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvMsg1.text = getString(R.string.stress_level_msg_1)
                        binding.ivImage1.setImageResource(R.drawable.stress_level_img_1)
                        binding.tvMsg2.text = getString(R.string.stress_level_msg_2)
                        binding.ivImage2.setImageResource(R.drawable.stress_level_img_2)
                        binding.ivImage2.visibility = View.VISIBLE
                        binding.tvMsg3.text = getString(R.string.stress_level_msg_3)
                        binding.tvMsg3.visibility = View.VISIBLE
                    }
                    Enums.ResultType.RECOVERY_ABILITY.type -> {
                        binding.tvMsg1.text = getString(R.string.recovery_ability_msg_1)
                        binding.ivImage1.setImageResource(R.drawable.stress_response_img_1)
                        binding.tvMsg2.text = getString(R.string.recovery_ability_msg_2)
                    }
                    Enums.ResultType.STRESS_RESPONSE.type -> {
                        binding.tvMsg1.text = getString(R.string.stress_response_msg_1)
                        binding.ivImage1.setImageResource(R.drawable.stress_response_img_1)
                        binding.tvMsg2.text = getString(R.string.stress_response_msg_2)
                    }
                    Enums.ResultType.OXYGEN_SATURATION.type -> {
                        binding.tvMsg1.text = getString(R.string.oxygen_saturation_msg_1)
                        binding.ivImage1.setImageResource(R.drawable.oxygen_saturation_img_1)
                        binding.tvMsg2.text = getString(R.string.oxygen_saturation_msg_2)
                    }
                    Enums.ResultType.PRQ.type -> {
                        binding.tvMsg1.text = getString(R.string.prq_msg_1)
                        binding.ivImage1.setImageResource(R.drawable.prq_img_1)
                        binding.tvMsg2.text = getString(R.string.prq_msg_2)
                    }
                    Enums.ResultType.BREATH_RATE.type -> {
                        binding.tvMsg1.text = getString(R.string.breath_rate_msg_1)
                        binding.ivImage1.setImageResource(R.drawable.breathing_rate_img_1)
                        binding.tvMsg2.text = getString(R.string.breath_rate_msg_2)
                    }
                    Enums.ResultType.BLOOD_PRESSURE.type -> {
                        binding.tvMsg1.text = getString(R.string.blood_pressure_msg_1)
                        binding.ivImage1.setImageResource(R.drawable.blood_pressure_img_1)
                        binding.tvMsg2.text = getString(R.string.blood_pressure_msg_2)
                    }
                }
            }

        } else {
            CommonUtils.showToast(this, getString(R.string.no_result_found))
            finish()
        }
    }
}