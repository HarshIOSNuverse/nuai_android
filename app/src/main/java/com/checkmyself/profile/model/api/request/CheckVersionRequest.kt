package com.checkmyself.profile.model.api.request

import com.google.gson.annotations.SerializedName

data class CheckVersionRequest constructor(
    @SerializedName("device") var device: String? = "android",
    @SerializedName("version") var version: String? = "",
)