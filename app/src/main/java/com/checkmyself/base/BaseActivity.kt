package com.checkmyself.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.DisplayMetrics
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.checkmyself.databinding.LayoutNoInternetBinding
import com.checkmyself.databinding.ToolbarLayoutBinding
import com.checkmyself.home.ui.activity.ScanByFaceActivity
import com.checkmyself.home.ui.activity.ScanByFingerActivity
import com.checkmyself.onboarding.ui.activity.LoginRegisterActivity
import com.checkmyself.onboarding.ui.activity.SplashActivity
import com.checkmyself.onboarding.ui.activity.SplashAnimationActivity
import com.checkmyself.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import com.checkmyself.R

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    companion object {
        val TAG: String = BaseActivity::class.java.simpleName
        private const val RP_LOCATION = 34
        const val RP_CAMERA = 35
    }

    private lateinit var toolbarLayoutBinding: ToolbarLayoutBinding
    private lateinit var tvToolbarTitle: AppCompatTextView
    private lateinit var toolbarIcon: ImageView

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (this is SplashActivity || this is SplashAnimationActivity
            || this is LoginRegisterActivity
        ) {

//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
//            controller.hide(WindowInsetsCompat.Type.systemBars())
//            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun attachBaseContext(newBase: Context?) {
        val myLocale = Locale(Pref.appLocale)
        val context = ContextWrapper.wrap(newBase!!, myLocale)
        super.attachBaseContext(context)
    }

    protected fun setUpToolNewBar(toolbarLayout: ToolbarLayoutBinding) {
        this.toolbarLayoutBinding = toolbarLayout
        val toolbar = toolbarLayout.toolbar.apply {
            setPadding(/*if(this@BaseActivity is HomeActivity) 22 else*/ 20, 0, 0, 0)
            setContentInsetsAbsolute(0, 0)
        }
        tvToolbarTitle = toolbarLayout.tvToolbarTitle
        toolbarIcon = toolbarLayout.toolbarIcon
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(ContextCompat.getDrawable(this@BaseActivity, R.drawable.ic_back))
        }
    }

    protected fun changeToolBarBgColor(color: Int) {
        toolbarLayoutBinding.toolbarRoot.setBackgroundColor(ContextCompat.getColor(this, color))
    }

    protected fun showToolbarIcon(show: Boolean = false) {
        if (show) {
            toolbarIcon.visibility = View.VISIBLE
            tvToolbarTitle.typeface = ResourcesCompat.getFont(this, R.font.switzer_bold)
            tvToolbarTitle.setTextSize(COMPLEX_UNIT_SP, 19f)
        } else {
            tvToolbarTitle.typeface = ResourcesCompat.getFont(this, R.font.switzer_regular)
            tvToolbarTitle.setTextSize(COMPLEX_UNIT_SP, 17f)
            toolbarIcon.visibility = View.GONE
        }
    }

    protected fun setToolBarTitle(title: String?) {
        tvToolbarTitle.text = if (!title.isNullOrEmpty()) title else ""
    }

    protected fun hideBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    protected fun initNoInternet(
        noInternetLayout: LayoutNoInternetBinding, onClickListener: View.OnClickListener
    ) {
        noInternetLayout.onClickListener = onClickListener
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun finish() {
        super.finish()
        AnimationsHandler.playBaseActivityAnimation(this)
    }

    protected fun hideKeyboard(v: EditText) {
        val imm: InputMethodManager =
            v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    protected fun showHideProgress(show: Boolean) {
        if (show) {
            CustomProgressDialog.showProgressDialog(this)
        } else {
            CustomProgressDialog.dismissProgressDialog()
        }
    }

    protected fun enableDisableButton(button: Button, isEnabled: Boolean) {
        if (isEnabled) {
            button.isEnabled = true
            button.setBackgroundResource(R.drawable.rc_black_filled_c25)
            button.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            button.isEnabled = false
            button.setBackgroundResource(R.drawable.rc_gray_filled_c25)
            button.setTextColor(
                ContextCompat.getColor(this, R.color.disabled_btn_text_color)
            )
        }
    }

    @Suppress("DEPRECATION")
    fun getScreenHeight(): Int {
        val displayMetrics = DisplayMetrics()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = display
            display?.getRealMetrics(displayMetrics)
        } else {
            windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        return displayMetrics.heightPixels
    }

    @Suppress("DEPRECATION")
    fun getScreenWidth(): Int {
        val displayMetrics = DisplayMetrics()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = display
            display?.getRealMetrics(displayMetrics)
        } else {
            windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        return displayMetrics.widthPixels
    }

    open fun getLocationOnScreen(view: View): Point? {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return Point(location[0], location[1])
    }

    fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    fun requestPermissions() {
        val shouldProvideRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        if (shouldProvideRationale) {
            startLocationPermissionRequest()
        } else {
            startLocationPermissionRequest()
        }
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), RP_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RP_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    getLastLocation()
                    startLocationUpdates()
                } else {
                    CommonUtils.showToast(
                        this, getString(R.string.you_cancel_location_permission)
                    )
                }

            }
            RP_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (this is ScanByFaceActivity) {
                        this.createHealthMonitorManager()
//                        createSession()
                    }
                    if (this is ScanByFingerActivity) {
                        this.createHealthMonitorManager()
//                        createSession()
                    }
                } else {
                    CommonUtils.showToast(this, getString(R.string.required_permission_not_granted))
                    finish()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        mFusedLocationClient?.lastLocation?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful && task.result != null) {
                // Need to uncomment
                val lat = task.result?.latitude!!
                val lng = task.result?.longitude!!
                Pref.currentLatitude = lat
                Pref.currentLongitude = lng
            } else {
                Logger.d(TAG, "getLastLocation:exception" + task.exception)
                startLocationUpdates()
            }
        }
    }

    protected fun startLocationUpdates() {
        if (locationRequest == null || locationCallback == null) {
            locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10 * 1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(1000)
                .setMaxUpdateDelayMillis(10 * 1000)
                .build()
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
//                    if (locationResult == null)
//                        return
                    super.onLocationResult(locationResult)
                    for (location: Location in locationResult.locations) {
                        Pref.currentLatitude = location.latitude
                        Pref.currentLongitude = location.longitude

                    }
                    stopLocationUpdates()
                }
            }
        }
        requestLocationSettings(locationRequest!!, locationCallback!!)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationSettings(
        locationRequest: LocationRequest,
        locationCallback: LocationCallback
    ) {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val client = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this) {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mFusedLocationClient!!.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.getMainLooper()
                )
            }
        }.addOnFailureListener {
            if (it is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    checkSettingLauncher.launch(IntentSenderRequest.Builder(it.resolution).build())
//                    it.startResolutionForResult(
//                        this, REQUEST_CHECK_SETTINGS
//                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    sendEx.printStackTrace()
                }
            }
        }
    }


    //
    private fun stopLocationUpdates() {
        if (mFusedLocationClient != null && locationCallback != null) {
            mFusedLocationClient?.removeLocationUpdates(locationCallback!!)
        }
    }

    private val checkSettingLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    startLocationUpdates()
                }
                Activity.RESULT_CANCELED -> {
                    CommonUtils.showToast(this, getString(R.string.you_cancel_location_permission))
//                    startLocationUpdates()
                }
            }
        }

}