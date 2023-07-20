package com.checkmyself.base

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.checkmyself.R
import com.checkmyself.databinding.LayoutNoInternetBinding
import com.checkmyself.databinding.ToolbarLayoutBinding
//import com.checkmyself.home.ui.activity.ScanByFaceActivity
//import com.checkmyself.home.ui.activity.ScanByFingerActivity
import com.checkmyself.onboarding.ui.activity.LoginRegisterActivity
import com.checkmyself.onboarding.ui.activity.SplashActivity
import com.checkmyself.onboarding.ui.activity.SplashAnimationActivity
import com.checkmyself.utils.*
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    companion object {
        val TAG: String = BaseActivity::class.java.simpleName
        const val RP_CAMERA = 35
    }

    private lateinit var toolbarLayoutBinding: ToolbarLayoutBinding
    private lateinit var tvToolbarTitle: AppCompatTextView
    private lateinit var toolbarIcon: ImageView

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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RP_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*if (this is ScanByFaceActivity) {
                        this.createHealthMonitorManager()
//                        createSession()
                    }
                    if (this is ScanByFingerActivity) {
                        this.createHealthMonitorManager()
//                        createSession()
                    }*/
                } else {
                    CommonUtils.showToast(this, getString(R.string.required_permission_not_granted))
                    finish()
                }
            }
        }
    }

}