package com.nuai.onboarding.model.api.response

import com.google.gson.annotations.SerializedName
import com.nuai.network.CommonResponse

class LoginResponse : CommonResponse() {

    @SerializedName("id")
    var id = 0
}