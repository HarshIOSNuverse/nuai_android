package com.checkmyself.network

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

open class BaseResponse() : Parcelable{

    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("response")
    var response: String? = ""

    @SerializedName("error_message")
    var errorMessage: String? = ""

    constructor(parcel: Parcel) : this() {
        status = parcel.readString()
        message = parcel.readString()
        response = parcel.readString()
        errorMessage = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(status)
        parcel.writeString(message)
        parcel.writeString(response)
        parcel.writeString(errorMessage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BaseResponse> {
        override fun createFromParcel(parcel: Parcel): BaseResponse {
            return BaseResponse(parcel)
        }

        override fun newArray(size: Int): Array<BaseResponse?> {
            return arrayOfNulls(size)
        }
    }
}
