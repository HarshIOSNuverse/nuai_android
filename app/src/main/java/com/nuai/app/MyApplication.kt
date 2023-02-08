package com.nuai.app

import android.app.Application
import com.nuai.network.NetworkUtils
import com.nuai.utils.Pref
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    companion object {
        var instance: MyApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val accessToken = Pref.accessToken
        if (!accessToken.isNullOrEmpty()) {
            NetworkUtils.putAuthParam(NetworkUtils.AUTHORIZATION, "Bearer $accessToken")
        }
    }
}