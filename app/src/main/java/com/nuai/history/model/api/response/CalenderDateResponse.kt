package com.nuai.history.model.api.response

import com.google.gson.annotations.SerializedName
import com.nuai.network.CommonResponse
import com.nuai.profile.model.CalendarDate

class CalenderDateResponse : CommonResponse() {

    @SerializedName("info")
    var info: ArrayList<CalendarDate>? = null
}