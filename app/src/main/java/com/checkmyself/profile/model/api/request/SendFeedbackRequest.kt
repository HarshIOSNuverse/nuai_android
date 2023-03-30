package com.checkmyself.profile.model.api.request

import com.google.gson.annotations.SerializedName

data class SendFeedbackRequest(
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("rating") val rating: Float,
)