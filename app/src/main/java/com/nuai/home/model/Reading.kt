package com.nuai.home.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Reading() : Parcelable {
    @SerializedName("id")
    var id: Long = 0

    @SerializedName("title")
    var title: String? = null

    @SerializedName("short_desc")
    var shortDesc: String? = null

    @SerializedName("unit")
    var unit: String? = null

    @SerializedName("ordering")
    var ordering: Long = 0

    @SerializedName("group")
    var group: String? = null

    @SerializedName("observed_value")
    var observedValue: String? = null

    @SerializedName("level")
    var level: String? = null

    @SerializedName("level_icon")
    var levelIcon: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        title = parcel.readString()
        shortDesc = parcel.readString()
        unit = parcel.readString()
        ordering = parcel.readLong()
        group = parcel.readString()
        observedValue = parcel.readString()
        level = parcel.readString()
        levelIcon = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(shortDesc)
        parcel.writeString(unit)
        parcel.writeLong(ordering)
        parcel.writeString(group)
        parcel.writeString(observedValue)
        parcel.writeString(level)
        parcel.writeString(levelIcon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Reading> {
        override fun createFromParcel(parcel: Parcel): Reading {
            return Reading(parcel)
        }

        override fun newArray(size: Int): Array<Reading?> {
            return arrayOfNulls(size)
        }
    }
}