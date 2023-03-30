package com.checkmyself.onboarding.model.api.request

import com.google.gson.annotations.SerializedName

data class ResendOTPRequest constructor(
    @SerializedName("dial_code") val dialCode: String?,
    @SerializedName("phone_number") val phoneNumber: String?
)