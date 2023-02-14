package com.nuai.onboarding.model.api.response

import com.google.gson.annotations.SerializedName
import com.nuai.network.CommonResponse
import com.nuai.profile.model.User

class MyProfileResponse : CommonResponse() {

    @SerializedName("info")
    var user: User? = null
}