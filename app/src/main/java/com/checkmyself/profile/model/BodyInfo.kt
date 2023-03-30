package com.checkmyself.profile.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class BodyInfo() : Parcelable {
    @SerializedName("id")
    var id = 0

    @SerializedName("user_id")
    var userId: Long = 0

    @SerializedName("gender")
    var gender: String? = null

    @SerializedName("date_of_birth")
    var dateOfBirth: String? = null

    @SerializedName("weight")
    var weight: Double = 0.0

    @SerializedName("height")
    var height: Double = 0.0

    @SerializedName("bmi_index")
    var bmiIndex: Double = 0.0

    @SerializedName("category")
    var category: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        userId = parcel.readLong()
        gender = parcel.readString()
        dateOfBirth = parcel.readString()
        weight = parcel.readDouble()
        height = parcel.readDouble()
        bmiIndex = parcel.readDouble()
        category = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeLong(userId)
        parcel.writeString(gender)
        parcel.writeString(dateOfBirth)
        parcel.writeDouble(weight)
        parcel.writeDouble(height)
        parcel.writeDouble(bmiIndex)
        parcel.writeString(category)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyInfo> {
        override fun createFromParcel(parcel: Parcel): BodyInfo {
            return BodyInfo(parcel)
        }

        override fun newArray(size: Int): Array<BodyInfo?> {
            return arrayOfNulls(size)
        }
    }

}