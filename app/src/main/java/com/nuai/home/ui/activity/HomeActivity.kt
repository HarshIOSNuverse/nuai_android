package com.nuai.home.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.HomeActivityBinding
import com.nuai.history.ui.activity.HealthHistoryListActivity
import com.nuai.interfaces.DialogClickListener
import com.nuai.onboarding.ui.activity.LoginRegisterActivity
import com.nuai.profile.ui.activity.ProfileActivity
import com.nuai.settings.ui.activity.SettingsActivity
import com.nuai.utils.AlertDialogManager
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.Pref
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.run {
                activity.startActivity(this)
                activity.finish()
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: HomeActivityBinding

    //    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var actionBarToggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.home_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.app_name))
        showToolbarIcon(true)
        initClickListener()
//        initObserver()
        init()
    }

    private fun init() {
        Handler(Looper.getMainLooper()).postDelayed({
            setUpDrawerView()
        }, 500)
    }

    override fun onResume() {
        super.onResume()
        setUserDetails()
    }

    private fun setUserDetails() {
        val user = Pref.user
        binding.navigationMenuContainer.user = user
    }

//    private fun initObserver() {
//        lifecycleScope.launch {
//            profileViewModel.meApiState.collect {
//                when (it.status) {
//                    Status.LOADING -> {
//                        showHideProgress(it.data == null)
//                    }
//                    Status.SUCCESS -> {
//                        if (it.data != null && (it.code == ResponseStatus.STATUS_CODE_SUCCESS)) {
//                        }
//                    }
//                    Status.ERROR -> {
//                        showHideProgress(false)
//                        CommonUtils.showToast(this@HomeActivity, it.message)
//                    }
//                }
//            }
//        }
//    }

    private fun initClickListener() {
        binding.onClickListener = this
        binding.navigationMenuContainer.onClickLister = this
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ll_health_scan -> {
                HealthScanOptionsActivity.startActivity(this)
            }
            R.id.ll_health_history -> {
                HealthHistoryListActivity.startActivity(this)
            }
            R.id.view_profile_text -> {
                binding.drawer.closeDrawer(binding.nv)
                ProfileActivity.startActivityForResult(this, profileLauncher)
            }

            R.id.health_history_menu -> {
                binding.drawer.closeDrawer(binding.nv)
                HealthHistoryListActivity.startActivity(this)
            }
            R.id.settings_menu -> {
                binding.drawer.closeDrawer(binding.nv)
                SettingsActivity.startActivity(this)
            }
            R.id.about_app_menu -> {
                binding.drawer.closeDrawer(binding.nv)
                AboutAppActivity.startActivity(this)
            }
            R.id.logout_menu -> {
                AlertDialogManager.showConfirmationDialog(
                    this, getString(R.string.app_name),
                    getString(R.string.are_you_sure_want_to_logout),
                    getString(R.string.logout), ContextCompat.getColor(this, R.color.white),
                    R.drawable.rc_red_filled_c25, getString(R.string.no),
                    ContextCompat.getColor(this, R.color.white),
                    R.drawable.rc_black_filled_c25, false,
                    object : DialogClickListener {
                        override fun onButton1Clicked() {
                            logout()
                        }

                        override fun onButton2Clicked() {
                        }

                        override fun onCloseClicked() {

                        }
                    })
            }
        }
    }

    private fun logout() {
        Pref.logout()
        LoginRegisterActivity.clearTopAndOpenLoginSignUpActivity(this@HomeActivity)
        return
    }

    private fun setUpDrawerView() {
        actionBarToggle =
            ActionBarDrawerToggle(this, binding.drawer, R.string.app_name, R.string.app_name)
        binding.drawer.addDrawerListener(actionBarToggle)
        actionBarToggle.syncState()
        supportActionBar!!.setHomeAsUpIndicator(
            ContextCompat.getDrawable(this, R.drawable.nav_menu_icon)
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarToggle.onOptionsItemSelected(item)) true
        else super.onOptionsItemSelected(item)
    }

    private val profileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                setUserDetails()
            }
        }
}