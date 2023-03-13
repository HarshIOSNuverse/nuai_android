package com.nuai.base

import android.content.Context
import android.os.Bundle
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
import com.nuai.R
import com.nuai.databinding.LayoutNoInternetBinding
import com.nuai.databinding.ToolbarLayoutBinding
import com.nuai.onboarding.ui.activity.LoginRegisterActivity
import com.nuai.onboarding.ui.activity.SplashActivity
import com.nuai.onboarding.ui.activity.SplashAnimationActivity
import com.nuai.utils.AnimationsHandler
import com.nuai.utils.ContextWrapper
import com.nuai.utils.CustomProgressDialog
import com.nuai.utils.Pref
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    companion object {
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
            setPadding(0, 0, 0, 0)
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
        } else {
            tvToolbarTitle.typeface = ResourcesCompat.getFont(this, R.font.switzer_regular)
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
}