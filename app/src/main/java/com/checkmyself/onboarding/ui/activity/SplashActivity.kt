package com.checkmyself.onboarding.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.checkmyself.R
import com.checkmyself.base.BaseActivity
import com.checkmyself.databinding.SplashActivityBinding
import java.util.Locale

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private lateinit var binding: SplashActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.splash_activity)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

//        val price1 = (7990000.toDouble() / 1000000).toDouble()
//        Log.e("price1", "" + price1)
//
//        val numberOfFraction = 2
//        val formattedPrice = String.format(Locale.US, "%,.${numberOfFraction}f", price1)
//        Log.e("formattedPrice", formattedPrice)
//        Toast.makeText(this, "price = $price1", Toast.LENGTH_SHORT).show()


        Handler(Looper.getMainLooper()).postDelayed({
            SplashAnimationActivity.startActivity(
                this
            )
        }, 1000)
    }
}