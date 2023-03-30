package com.checkmyself.network

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Error() : Parcelable {

    var message: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("next")
    var next: String? = null

    constructor(parcel: Parcel) : this() {
        message = parcel.readString()
        status = parcel.readString()
        next = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
        parcel.writeString(status)
        parcel.writeString(next)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Error> {
        override fun createFromParcel(parcel: Parcel): Error {
            return Error(parcel)
        }

        override fun newArray(size: Int): Array<Error?> {
            return arrayOfNulls(size)
        }
    }
}