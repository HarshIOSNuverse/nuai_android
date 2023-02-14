package com.nuai.profile.model

import android.os.Parcel
import android.os.Parcelable
import com.nuai.network.BaseResponse
import com.google.gson.annotations.SerializedName

class User() : BaseResponse() {

    @SerializedName("id")
    var id = 0

    var uuid: String? = null

    @JvmField
    @SerializedName("full_name")
    var fullName: String? = null

    @SerializedName("first_name")
    var firstName: String? = null

    @SerializedName("last_name")
    var lastName: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("email_verified_at")
    var emailVerifiedAt: String? = null

    @SerializedName("deleted_at")
    var deletedAt: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        uuid = parcel.readString()
        fullName = parcel.readString()
        firstName = parcel.readString()
        lastName = parcel.readString()
        email = parcel.readString()
        emailVerifiedAt = parcel.readString()
        deletedAt = parcel.readString()
        createdAt = parcel.readString()
        updatedAt = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(id)
        parcel.writeString(uuid)
        parcel.writeString(fullName)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(email)
        parcel.writeString(emailVerifiedAt)
        parcel.writeString(deletedAt)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}