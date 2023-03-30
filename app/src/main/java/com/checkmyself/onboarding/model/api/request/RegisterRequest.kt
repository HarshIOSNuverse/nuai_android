package com.checkmyself.onboarding.model.api.request

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("date_of_birth") val dateOfBirth: String?,
    @SerializedName("gender") val gender: String,
    @SerializedName("weight") val weight: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("password") var password: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(email)
        parcel.writeString(dateOfBirth)
        parcel.writeString(gender)
        parcel.writeInt(weight)
        parcel.writeInt(height)
        parcel.writeString(password)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RegisterRequest> {
        override fun createFromParcel(parcel: Parcel): RegisterRequest {
            return RegisterRequest(parcel)
        }

        override fun newArray(size: Int): Array<RegisterRequest?> {
            return arrayOfNulls(size)
        }
    }
}
