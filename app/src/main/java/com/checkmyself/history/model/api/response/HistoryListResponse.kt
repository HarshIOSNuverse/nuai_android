package com.checkmyself.history.model.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.checkmyself.home.model.HealthBasicInfo
import com.checkmyself.network.CommonResponse

class HistoryListResponse : CommonResponse() {

    @SerializedName("data")
    var data: Data? = null
}

class Data() : Parcelable {
    @SerializedName("log_date")
    var logDate: String? = null

    @SerializedName("scan_results")
    var scanResults: ArrayList<HealthBasicInfo>? = null

    constructor(parcel: Parcel) : this() {
        logDate = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(logDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }
}