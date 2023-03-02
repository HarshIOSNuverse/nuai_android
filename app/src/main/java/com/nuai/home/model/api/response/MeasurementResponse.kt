package com.nuai.home.model.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.nuai.home.model.HealthBasicInfo
import com.nuai.home.model.Reading
import com.nuai.network.CommonResponse

class MeasurementResponse() : CommonResponse(), Parcelable {

    @SerializedName("basic_info")
    var basicInfo: HealthBasicInfo? = null

    @SerializedName("result")
    var result: Result? = null

    @SerializedName("ABNORMAL_RESULT")
    var abnormalResult: Result? = null

    constructor(parcel: Parcel) : this() {
        basicInfo = parcel.readParcelable(HealthBasicInfo::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(basicInfo, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MeasurementResponse> {
        override fun createFromParcel(parcel: Parcel): MeasurementResponse {
            return MeasurementResponse(parcel)
        }

        override fun newArray(size: Int): Array<MeasurementResponse?> {
            return arrayOfNulls(size)
        }
    }
}

class Result {
    @SerializedName("VITAL_SIGNS")
    var vitalSigns: ArrayList<Reading>? = null

    @SerializedName("BLOOD")
    var blood: ArrayList<Reading>? = null

    @SerializedName("STRESS_LEVEL")
    var stressLevel: ArrayList<Reading>? = null

    @SerializedName("ENERGY")
    var energy: ArrayList<Reading>? = null

    @SerializedName("HEART_RATE_VARIABILITY")
    var heartRateVariability: ArrayList<Reading>? = null

    @SerializedName("BLOOD_TEST")
    var bloodTest: ArrayList<Reading>? = null
}
