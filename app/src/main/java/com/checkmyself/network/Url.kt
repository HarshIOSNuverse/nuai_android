package com.checkmyself.network

import com.checkmyself.BuildConfig

object Url {
    var HOST =
        BuildConfig.BASE_URL_PROTOCOL.lowercase() + "://" + BuildConfig.BASE_URL + "/"
    const val API = "api/"
}