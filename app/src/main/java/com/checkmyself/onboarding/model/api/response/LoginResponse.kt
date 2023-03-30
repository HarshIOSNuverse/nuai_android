package com.checkmyself.onboarding.model.api.response

import com.google.gson.annotations.SerializedName
import com.checkmyself.network.CommonResponse

class LoginResponse : CommonResponse() {

    @SerializedName("id")
    var id = 0
}