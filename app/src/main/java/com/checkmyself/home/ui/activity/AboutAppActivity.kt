package com.checkmyself.home.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.checkmyself.BuildConfig
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.AboutAppActivityBinding
import com.checkmyself.onboarding.ui.activity.WebActivity
import com.checkmyself.utils.AnimationsHandler
import com.checkmyself.utils.CustomTypefaceSpan
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AboutAppActivity : BaseActivity() {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, AboutAppActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: AboutAppActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.about_app_activity)
        setUpToolNewBar(binding.toolbarLayout)
        setToolBarTitle(getString(R.string.about_the_app))
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        binding.appVersionSdkVersionText.text =
            "${getString(R.string.app_version)} ${BuildConfig.APP_VERSION} (${BuildConfig.VERSION_CODE}) | ${
                getString(
                    R.string.sdk_version
                )
            } ${ai.binah.sdk.BuildConfig.VERSION_NAME} (${ai.binah.sdk.BuildConfig.VERSION_CODE})"

//        binding.appVersionSdkVersionText.text =
//            "${getString(R.string.app_version)} ${BuildConfig.APP_VERSION} (${BuildConfig.VERSION_CODE}) | ${
//                getString(
//                    R.string.sdk_version
//                )
//            } )"

        setSpannableColor(
            binding.linkMessageText,
            getString(R.string.about_app_link_text),
            "https://carepointdevelopment.eu/",
            ContextCompat.getColor(this, R.color.blue_text_color)
        )
    }

    private fun setSpannableColor(
        view: TextView, fulltext: String, subtext1: String, color: Int
    ) {
        val regular = ResourcesCompat.getFont(this, R.font.switzer_regular)
        val str: Spannable = SpannableString(fulltext)
        val i1 = fulltext.indexOf(subtext1)
        str.setSpan(
            CustomTypefaceSpan("", regular), i1, i1 + subtext1.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        str.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                WebActivity.startActivity(
                    this@AboutAppActivity,
                    getString(R.string.app_name),
                    subtext1
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }, i1, i1 + subtext1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        str.setSpan(
            ForegroundColorSpan(color), i1, i1 + subtext1.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        view.movementMethod = LinkMovementMethod.getInstance()
        view.text = str
        view.highlightColor = Color.TRANSPARENT
    }

}