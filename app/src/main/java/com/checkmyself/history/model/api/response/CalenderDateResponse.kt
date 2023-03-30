package com.checkmyself.history.model.api.response

import com.google.gson.annotations.SerializedName
import com.checkmyself.network.CommonResponse
import com.checkmyself.profile.model.CalendarDate

class CalenderDateResponse : CommonResponse() {

    @SerializedName("info")
    var info: ArrayList<CalendarDate>? = null
}