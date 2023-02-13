package com.nuai.onboarding.ui.activity

import android.animation.*
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.SplashAnimationActivityBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashAnimationActivity : BaseActivity() {
    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, SplashAnimationActivity::class.java).run {
                activity.startActivity(this)
                activity.finish()
                activity.overridePendingTransition(0, 0)
            }
        }
    }

    private lateinit var binding: SplashAnimationActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.splash_animation_activity)
        setAlphaAnimation()
    }

    private fun setAlphaAnimation() {
        val fadeOut = ObjectAnimator.ofFloat(binding.ivImage, "alpha", 1f, .0f)
        fadeOut.duration = 1700
        val fadeIn = ObjectAnimator.ofFloat(binding.ivBgImage, "alpha", .0f, 1f)
        fadeIn.duration = 1800
        val zoomIn = ValueAnimator.ofFloat(1f, 20f)
        zoomIn.duration = 1700
        zoomIn.addUpdateListener { animation ->
            binding.ivImage.scaleX = animation.animatedValue as Float
            binding.ivImage.scaleY = animation.animatedValue as Float
        }
        val mAnimationSet = AnimatorSet()
        mAnimationSet.playTogether(fadeIn, zoomIn, fadeOut)
        mAnimationSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                LoginRegisterActivity.startActivity(this@SplashAnimationActivity)
            }
        })
        mAnimationSet.start()
    }
}