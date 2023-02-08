package com.nuai.network

import com.nuai.BuildConfig

object Url {
    var HOST =
        BuildConfig.BASE_URL_PROTOCOL.lowercase() + "://" + BuildConfig.BASE_URL + "/"
    const val API = "api/"
}