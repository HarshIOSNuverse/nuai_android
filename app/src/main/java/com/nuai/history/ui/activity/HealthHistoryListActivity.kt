package com.nuai.history.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.HealthHistoryListActivityBinding
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.Pref
import dagger.hilt.android.AndroidEntryPoint


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
    private val user = Pref.user
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.health_history_list_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.app_name))
        showToolbarIcon(true)
        initClickListener()
        init()
    }

    private fun init() {
    }

    private fun initClickListener() {
        binding.onClickListener = this
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.measure_now_btn -> {
            }
        }
    }

}