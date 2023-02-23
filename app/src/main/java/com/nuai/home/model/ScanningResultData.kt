package com.nuai.home.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import java.io.Serializable

class ScanningResultData : Serializable {
    @SerializedName("id")
    @Expose
    var id = 0

    @SerializedName("scan_by")
    @Expose
    var scanBy: String? = null

    @SerializedName("heart_rate")
    @Expose
    var heartRate = "0"

    @SerializedName("oxygen_saturation")
    @Expose
    var oxygenSaturation = "0"

    @SerializedName("respiration")
    @Expose
    var respiration = "0"

    @SerializedName("stress_level")
    @Expose
    var stressLevel = 0

    @SerializedName("stress_five_levels")
    @Expose
    var stressFiveLevels = ""

    @SerializedName("hrv_sdnn")
    @Expose
    var hrvSdnn = "0"

    @SerializedName("blood_pressure")
    @Expose
    var bloodPressure = "0"

    @SerializedName("bmi")
    @Expose
    var bmi = ""

    @SerializedName("bmi_category")
    @Expose
    var bmiCategory = ""

    @SerializedName("result_date")
    @Expose
    var resultDate: String? = null

    @SerializedName("result_time")
    @Expose
    var resultTime: String? = null

    @SerializedName("hemoglobin")
    @Expose
    var hemoglobin: String? = null

    @SerializedName("hba1c")
    @Expose
    var hba1c: String? = null
    var latitude = 0.0
    var longitude = 0.0
}