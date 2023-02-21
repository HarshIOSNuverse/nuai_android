package com.nuai.profile.model

import com.google.gson.annotations.SerializedName

class CalendarDate {
    @SerializedName("date")
    var date: String? = null// "2023-02-01",

    @SerializedName("score")
    var score: Int? = null // 3
}