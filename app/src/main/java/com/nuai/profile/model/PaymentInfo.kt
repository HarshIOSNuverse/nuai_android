package com.nuai.profile.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class PaymentInfo() : Parcelable {

    @SerializedName("id")
    var id: Long = 0

    @SerializedName("uuid")
    var uuid: String? = null

    @SerializedName("scan_by")
    var scanBy: String? = ""

    @SerializedName("heart_rate")
    var heartRate: Long = 0

    @SerializedName("breathing_rate")
    var breathingRate: Long = 0

    @SerializedName("prq")
    var prq: Double = 0.0

    @SerializedName("oxygen_saturation")
    var oxygenSaturation: Long = 0

    @SerializedName("blood_pressure")
    var bloodPressure: String? = ""

    @SerializedName("recovery_ability")
    var recoveryAbility: String? = null

    @SerializedName("stress_response")
    var stressResponse: String? = null

    @SerializedName("respiration")
    var respiration: Int = 0

    @SerializedName("stress_level")
    var stressLevel: String? = null

    @SerializedName("hrv_sdnn")
    var hrvSdnn: Int = 0

    @SerializedName("hemoglobin")
    var hemoglobin: Double = 0.0

    @SerializedName("hba1c")
    var hba1c: Double = 0.0

    @SerializedName("wellness_score")
    var wellnessScore: Int = 0

    @SerializedName("latitude")
    var latitude: Double = 0.0

    @SerializedName("longitude")
    var longitude: Double = 0.0

    @SerializedName("result_date")
    var resultDate: String? = ""

    @SerializedName("result_time")
    var resultTime: String? = null

    @SerializedName("created_at")
    var createdAt: String? = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        uuid = parcel.readString()
        scanBy = parcel.readString()
        heartRate = parcel.readLong()
        breathingRate = parcel.readLong()
        prq = parcel.readDouble()
        oxygenSaturation = parcel.readLong()
        bloodPressure = parcel.readString()
        recoveryAbility = parcel.readString()
        stressResponse = parcel.readString()
        respiration = parcel.readInt()
        stressLevel = parcel.readString()
        hrvSdnn = parcel.readInt()
        hemoglobin = parcel.readDouble()
        hba1c = parcel.readDouble()
        wellnessScore = parcel.readInt()
        latitude = parcel.readDouble()
        longitude = parcel.readDouble()
        resultDate = parcel.readString()
        resultTime = parcel.readString()
        createdAt = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(uuid)
        parcel.writeString(scanBy)
        parcel.writeLong(heartRate)
        parcel.writeLong(breathingRate)
        parcel.writeDouble(prq)
        parcel.writeLong(oxygenSaturation)
        parcel.writeString(bloodPressure)
        parcel.writeString(recoveryAbility)
        parcel.writeString(stressResponse)
        parcel.writeInt(respiration)
        parcel.writeString(stressLevel)
        parcel.writeInt(hrvSdnn)
        parcel.writeDouble(hemoglobin)
        parcel.writeDouble(hba1c)
        parcel.writeInt(wellnessScore)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(resultDate)
        parcel.writeString(resultTime)
        parcel.writeString(createdAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentInfo> {
        override fun createFromParcel(parcel: Parcel): PaymentInfo {
            return PaymentInfo(parcel)
        }

        override fun newArray(size: Int): Array<PaymentInfo?> {
            return arrayOfNulls(size)
        }
    }

}

