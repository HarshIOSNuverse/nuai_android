package com.nuai.onboarding.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.nuai.R
import com.nuai.base.BaseActivity
import com.nuai.databinding.WebActivityBinding
import com.nuai.utils.IntentConstant
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun startActivity(activity: Activity, title: String, url: String) {
            Intent(activity, WebActivity::class.java).apply {
                putExtra(IntentConstant.TITLE, title)
                putExtra(IntentConstant.URL, url)
            }.run {
                activity.startActivity(this)
            }
        }

        fun startActivity(activity: Activity, title: String, url: String, type: String) {
            Intent(activity, WebActivity::class.java).apply {
                putExtra(IntentConstant.TITLE, title)
                putExtra(IntentConstant.URL, url)
                putExtra(IntentConstant.TYPE, type)
            }.run {
                activity.startActivity(this)
            }
        }
    }

    private lateinit var url: String
    private var type1: String? = ""
    private lateinit var binding: WebActivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.web_activity)
        setUpToolNewBar(binding.toolbarLayout)
        init()
    }

    private fun init() {
        intent.run {
            url = getStringExtra(IntentConstant.URL) as String
            type1 = getStringExtra(IntentConstant.TYPE) as String?
            setToolBarTitle(getStringExtra(IntentConstant.TITLE))
        }
        loadWeb()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWeb() {
        val webSetting = binding.webView.settings
        webSetting.javaScriptEnabled = true
        webSetting.displayZoomControls = true
        webSetting.domStorageEnabled = true

        binding.webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                binding.progressBar.visibility = View.VISIBLE
                view.loadUrl(url)
                return true
            }
        }


        binding.progressBar.visibility = View.VISIBLE
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    binding.progressBar.visibility = View.GONE
                }

            }
        }
        binding.webView.loadUrl(url)
    }

    override fun onPause() {
        super.onPause()
        binding.webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
    }

    override fun onClick(v: View?) {

    }
}
