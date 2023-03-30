package com.checkmyself.onboarding.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.SplashActivityBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private lateinit var binding: SplashActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.splash_activity)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Handler(Looper.getMainLooper()).postDelayed({
            SplashAnimationActivity.startActivity(
                this
            )
        }, 1000)
    }
}