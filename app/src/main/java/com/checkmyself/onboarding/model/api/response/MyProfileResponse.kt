package com.checkmyself.onboarding.model.api.response

import com.google.gson.annotations.SerializedName
import com.checkmyself.network.CommonResponse
import com.checkmyself.profile.model.User

class MyProfileResponse : CommonResponse() {

    @SerializedName("info")
    var user: User? = null
}