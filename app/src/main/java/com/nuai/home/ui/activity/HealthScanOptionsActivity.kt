package com.nuai.home.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.HealthScanOptionsActivityBinding
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.Pref
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HealthScanOptionsActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, HealthScanOptionsActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: HealthScanOptionsActivityBinding
    private val user = Pref.user
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.health_scan_options_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.app_name))
        showToolbarIcon(true)
        initClickListener()
        init()
    }

    private fun init() {
        updateView(binding.rgType.checkedRadioButtonId)
    }

    private fun initClickListener() {
        binding.onClickListener = this
        binding.rgType.setOnCheckedChangeListener { _, checkedId ->
            updateView(checkedId)
        }
        enableDisableButton(binding.measureNowBtn, true)
    }

    private fun updateView(checkedId: Int) {
        if (checkedId == R.id.rb_face) {
            binding.ivTopIcon.setImageResource(R.drawable.face_option_select_top_icon)
            if (!user?.firstName.isNullOrEmpty()) {
                binding.tvTitle.text =
                    String.format(getString(R.string.face_option_select_title), user?.firstName)
            }
            binding.tvMessage.text = getString(R.string.face_option_select_msg)
        } else {
            binding.ivTopIcon.setImageResource(R.drawable.finger_option_select_top_icon)
            binding.tvTitle.text = getString(R.string.finger_option_select_title)
            binding.tvMessage.text = getString(R.string.finger_option_select_msg)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.measure_now_btn -> {
                ScanByFaceActivity.startActivity(this)
            }
        }
    }

}