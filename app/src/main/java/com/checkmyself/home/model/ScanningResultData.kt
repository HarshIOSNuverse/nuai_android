package com.checkmyself.home.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import java.io.Serializable

class ScanningResultData : Serializable {
    @SerializedName("scan_by")
    @Expose
    var scanBy: String? = null

    @SerializedName("heart_rate")
    @Expose
    var heartRate = "0"

    @SerializedName("heart_rate_conf_level")
    @Expose
    var heartRateConfLevel: String? = null

    @SerializedName("oxygen_saturation")
    @Expose
    var oxygenSaturation = "0"

    @SerializedName("stress_level")
    @Expose
    var stressLevel: Int = 0

    @SerializedName("hrv_sdnn")
    @Expose
    var hrvSdnn = "0"

    @SerializedName("hrv_sdnn_conf_level")
    @Expose
    var hrvSdnnConfLevel: String? = null

    @SerializedName("blood_pressure")
    @Expose
    var bloodPressure = "0"

    @SerializedName("hemoglobin")
    @Expose
    var hemoglobin: String? = null

    @SerializedName("hba1c")
    @Expose
    var hba1c: String? = null

    @SerializedName("prq")
    @Expose
    var prq: String? = "0"

    @SerializedName("prq_conf_level")
    @Expose
    var prqConfLevel: String? = null

    @SerializedName("breathing_rate")
    @Expose
    var breathingRate: String? = "0"

    @SerializedName("breathing_rate_conf_level")
    @Expose
    var breathingRateConfLevel: String? = null

    @SerializedName("stress_response")
    @Expose
    var stressResponse: String? = "" //

    @SerializedName("recovery_ability")
    @Expose
    var recoveryAbility: String? = null

    @SerializedName("wellness_score")
    @Expose
    var wellnessScore: String? = ""
}