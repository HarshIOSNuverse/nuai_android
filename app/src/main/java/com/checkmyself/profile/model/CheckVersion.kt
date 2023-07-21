package com.checkmyself.profile.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class CheckVersion() : Parcelable {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("device_type")
    var deviceType: String? = null

    @SerializedName("version")
    var version: String? = null

    @SerializedName("need_force_update")
    var needForceUpdate: Int = 0

    @SerializedName("has_update_available")
    var hasUpdateAvailable: Boolean = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        deviceType = parcel.readString()
        version = parcel.readString()
        needForceUpdate = parcel.readInt()
        hasUpdateAvailable = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(deviceType)
        parcel.writeString(version)
        parcel.writeInt(needForceUpdate)
        parcel.writeByte(if (hasUpdateAvailable) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CheckVersion> {
        override fun createFromParcel(parcel: Parcel): CheckVersion {
            return CheckVersion(parcel)
        }

        override fun newArray(size: Int): Array<CheckVersion?> {
            return arrayOfNulls(size)
        }
    }

}