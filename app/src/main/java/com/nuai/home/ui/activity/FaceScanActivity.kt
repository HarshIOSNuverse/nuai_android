package com.nuai.home.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.FaceScanActivityBinding
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.CommonUtils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FaceScanActivity : BaseActivity() {
    companion object {
        const val REQ_CAMERA = 12
        fun startActivity(activity: Activity) {
            Intent(activity, FaceScanActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: FaceScanActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.face_scan_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.app_name))
        showToolbarIcon(true)
        if (checkCameraPermission()) {

        } else {
            requestCameraPermission()
        }
    }

    private fun checkCameraPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
                )
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA), REQ_CAMERA
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQ_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    CommonUtils.showToast(
                        this,
                        getString(R.string.you_cancel_camera_permission)
                    )
                    finish()
                }
            }
        }
    }
}
