package com.checkmyself.profile.model.api.request

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("date_of_birth") val dateOfBirth: String?,
    @SerializedName("gender") val gender: String,
    @SerializedName("weight") val weight: Double,
    @SerializedName("height") val height: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(dateOfBirth)
        parcel.writeString(gender)
        parcel.writeDouble(weight)
        parcel.writeDouble(height)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UpdateProfileRequest> {
        override fun createFromParcel(parcel: Parcel): UpdateProfileRequest {
            return UpdateProfileRequest(parcel)
        }

        override fun newArray(size: Int): Array<UpdateProfileRequest?> {
            return arrayOfNulls(size)
        }
    }
}
