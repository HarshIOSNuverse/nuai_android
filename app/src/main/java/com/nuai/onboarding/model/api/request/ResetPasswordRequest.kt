package com.nuai.onboarding.model.api.request

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("email") val email: String?,
    @SerializedName("password") val password: String?
)
