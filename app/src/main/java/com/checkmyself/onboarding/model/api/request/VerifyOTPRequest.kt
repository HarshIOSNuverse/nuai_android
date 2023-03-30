package com.checkmyself.onboarding.model.api.request

data class VerifyOTPRequest constructor(
    val email: String?, val otp: String
)