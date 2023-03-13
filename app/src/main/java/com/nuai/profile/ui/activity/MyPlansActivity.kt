package com.nuai.profile.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.MyPlansActivityBinding
import com.nuai.network.ResponseStatus
import com.nuai.network.Status
import com.nuai.profile.viewmodel.ProfileViewModel
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MyPlansActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, MyPlansActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }

        fun startActivityForResult(
            activity: Activity, profileLauncher: ActivityResultLauncher<Intent>
        ) {
            Intent(activity, MyPlansActivity::class.java).run {
                profileLauncher.launch(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: MyPlansActivityBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.my_plans_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.my_plan))
        initClickListener()
        initObserver()
        Handler(Looper.getMainLooper()).postDelayed({
            init()
        }, 100)
    }

    private fun init() {
    }

    private fun initObserver() {
        lifecycleScope.launch {
            profileViewModel.updateProfileState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }
                    Status.SUCCESS -> {
                        if (it.data != null
                            && (it.code == ResponseStatus.STATUS_CODE_SUCCESS
                                    || it.code == ResponseStatus.STATUS_CODE_CREATED)
                        ) {
                            setResult(Activity.RESULT_OK)
                            profileViewModel.getMe()
                        }
                    }
                    Status.ERROR -> {
                        showHideProgress(false)
                        CommonUtils.showToast(this@MyPlansActivity, it.message)
                    }
                }
            }
        }
    }

    private fun initClickListener() {
        binding.onClickListener = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.subscribe_now_btn -> {
//                profileViewModel.updateProfile(request)
            }
        }
    }
}