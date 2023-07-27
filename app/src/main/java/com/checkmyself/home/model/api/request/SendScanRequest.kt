package com.checkmyself.home.model.api.request

import com.google.gson.annotations.SerializedName

data class SendScanRequest(
    @SerializedName("blood_pressure") val bloodPressure: String?,
    @SerializedName("hba1c") val hba1c: String?,
    @SerializedName("heart_rate") val heartRate: String?,
    @SerializedName("hemoglobin") val hemoglobin: String?,
    @SerializedName("hrv_sdnn") val hrvSdnn: String?,
    @SerializedName("oxygen_saturation") val oxygenSaturation: String?,
    @SerializedName("scan_by") val scanBy: String?,
    @SerializedName("stress_level") val stressLevel: Int,
    @SerializedName("stress_response") val stressResponse: String?,
    @SerializedName("breathing_rate") val breathingRate:String?,
    @SerializedName("prq") val prq:String?,
    @SerializedName("wellness_score") val wellnessScore:String?,
    @SerializedName("recovery_ability") val recoveryAbility:String?,

    @SerializedName("heart_rate_conf_level") val heartRateConfLevel:String?,
    @SerializedName("breathing_rate_conf_level") val breathingRateConfLevel:String?,
    @SerializedName("prq_conf_level") val prqConfLevel:String?,
    @SerializedName("hrv_sdnn_conf_level") val hrvSdnnConfLevel:String?
)
{
//    "recovery_ability": "",
}