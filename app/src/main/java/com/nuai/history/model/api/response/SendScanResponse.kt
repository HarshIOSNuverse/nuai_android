package com.nuai.history.model.api.response

import com.google.gson.annotations.SerializedName
import com.nuai.network.CommonResponse
import com.nuai.profile.model.CalendarDate

class SendScanResponse : CommonResponse() {

    @SerializedName("id")
    var id: String? = null
}