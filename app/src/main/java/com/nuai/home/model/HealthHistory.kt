package com.nuai.home.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class HealthHistory() : Parcelable {

    @SerializedName("id")
    var id: Long = 0

    @SerializedName("scan_type")
    var scanType: String? = ""

    @SerializedName("wellness_score")
    var wellnessScore: String? = null

    @SerializedName("score")
    var score: String? = ""

    @SerializedName("time")
    var time: String? = null

    @SerializedName("heart_rate")
    var heartRate: Long = 0

    @SerializedName("breathing_rate")
    var breathingRate: Long = 0

    @SerializedName("prq")
    var prq: Long = 0

    @SerializedName("oxygen_saturation")
    var oxygenSaturation: Long = 0

    @SerializedName("blood_pressure")
    var bloodPressure: String? = ""

    @SerializedName("stress_level")
    var stressLevel: String? = null

    @SerializedName("recovery_ability")
    var recoveryAbility: String? = null

    @SerializedName("stress_response")
    var stressResponse: String? = null

    @SerializedName("hrv_sdnn")
    var hrvSdnn: Int = 0

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        scanType = parcel.readString()
        wellnessScore = parcel.readString()
        score = parcel.readString()
        time = parcel.readString()
        heartRate = parcel.readLong()
        breathingRate = parcel.readLong()
        prq = parcel.readLong()
        oxygenSaturation = parcel.readLong()
        bloodPressure = parcel.readString()
        stressLevel = parcel.readString()
        recoveryAbility = parcel.readString()
        stressResponse = parcel.readString()
        hrvSdnn = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(scanType)
        parcel.writeString(wellnessScore)
        parcel.writeString(score)
        parcel.writeString(time)
        parcel.writeLong(heartRate)
        parcel.writeLong(breathingRate)
        parcel.writeLong(prq)
        parcel.writeLong(oxygenSaturation)
        parcel.writeString(bloodPressure)
        parcel.writeString(stressLevel)
        parcel.writeString(recoveryAbility)
        parcel.writeString(stressResponse)
        parcel.writeInt(hrvSdnn)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HealthHistory> {
        override fun createFromParcel(parcel: Parcel): HealthHistory {
            return HealthHistory(parcel)
        }

        override fun newArray(size: Int): Array<HealthHistory?> {
            return arrayOfNulls(size)
        }
    }

}

