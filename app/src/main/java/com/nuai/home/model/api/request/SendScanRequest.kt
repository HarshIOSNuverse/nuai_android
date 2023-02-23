package com.nuai.home.model.api.request

import com.google.gson.annotations.SerializedName

data class SendScanRequest(
    @SerializedName("blood_pressure") val bloodPressure: String?,
    @SerializedName("hba1c") val hba1c: String?,
    @SerializedName("heart_rate") val heartRate: String?,
    @SerializedName("hemoglobin") val hemoglobin: String?,
    @SerializedName("hrv_sdnn") val hrvSdnn: String?,
    @SerializedName("oxygen_saturation") val oxygenSaturation: String?,
    @SerializedName("respiration") val respiration: String?,
    @SerializedName("scan_by") val scanBy: String?,
    @SerializedName("stress_level") val stressLevel: Int,
    @SerializedName("breathing_rate") val breathingRate:String?,
    @SerializedName("prq") val prq:String?,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
)
