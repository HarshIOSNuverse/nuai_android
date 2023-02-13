package com.nuai.onboarding.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.AcceptTermsAndConditionActivityBinding
import com.nuai.utils.AnimationsHandler
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AcceptTermsAndConditionActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, AcceptTermsAndConditionActivity::class.java).run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private lateinit var binding: AcceptTermsAndConditionActivityBinding
    private var isPasswordShow: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.accept_terms_and_condition_activity)
        initClickListener()

    }

    private fun initClickListener() {
        binding.onClickListener = this
        binding.chkAcceptTerms.setOnCheckedChangeListener { _, isChecked ->
            enableDisableButton(binding.continueBtn, isChecked)
        }
        setSpannableColor(
            binding.chkAcceptTerms,
            getString(R.string.term_check_full_text),
            getString(R.string.term_of_use),
            getString(R.string.privacy_policy),
            ContextCompat.getColor(this, R.color.blue_text_color)
        )
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.continue_btn -> {
                RegistrationCompletedActivity.startActivity(this)
            }
        }
    }

    private fun setSpannableColor(
        view: CheckBox, fulltext: String, subtext1: String, subtext2: String, color: Int
    ) {
        val str: Spannable = SpannableString(fulltext)
        val i1 = fulltext.indexOf(subtext1)
        str.setSpan(
            ForegroundColorSpan(color), i1, i1 + subtext1.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        str.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com")
//                    Uri.parse(BuildConfig.BASE_URL + BuildConfig.TERMS_CONDITIONS)
                ).run {
                    startActivity(this)
                }
            }
        }, i1, i1 + subtext1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val i2 = fulltext.indexOf(subtext2)
        str.setSpan(
            ForegroundColorSpan(color), i2, i2 + subtext2.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        str.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com")
//                    Uri.parse(BuildConfig.BASE_URL + BuildConfig.TERMS_CONDITIONS)
                ).run {
                    startActivity(this)
                }
            }
        }, i2, i2 + subtext2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        view.setText(str, TextView.BufferType.SPANNABLE)
        view.movementMethod = LinkMovementMethod.getInstance()
        view.highlightColor = Color.TRANSPARENT
    }

    override fun onBackPressed() {}
}