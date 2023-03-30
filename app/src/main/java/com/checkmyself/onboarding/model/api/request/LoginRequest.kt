package com.checkmyself.onboarding.model.api.request

data class LoginRequest constructor(
    val email: String?, val password: String
)