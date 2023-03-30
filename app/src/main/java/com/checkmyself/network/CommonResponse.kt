package com.checkmyself.network

import com.google.gson.annotations.SerializedName

open class CommonResponse : ResponseStatus() {

    @SerializedName("access_token")
    var accessToken: String? = null

    @SerializedName("error")
    var error: Error? = null

}