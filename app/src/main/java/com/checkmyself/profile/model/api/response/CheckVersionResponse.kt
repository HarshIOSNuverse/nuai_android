package com.checkmyself.profile.model.api.response

import com.checkmyself.network.CommonResponse
import com.checkmyself.profile.model.CheckVersion
import com.google.gson.annotations.SerializedName

class CheckVersionResponse : CommonResponse() {

    @SerializedName("code")
    var code: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("data")
    var data: CheckVersion? = null

}