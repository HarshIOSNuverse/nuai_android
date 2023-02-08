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
    @SerializedName("name")
    var fullName: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("dial_code")
    var dialCode: String? = null

    @SerializedName("country_code")
    var countryCode: String? = null

    @SerializedName("phone_number")
    var phoneNumber: String? = null

    @SerializedName("social_type")
    var socialType: String? = null

    @SerializedName("social_id")
    var socialId: String? = null

    @SerializedName("avatar")
    var avatar: String? = null

    @SerializedName("localization")
    var localization: String? = null

    @SerializedName("phone_number_verified_at")
    var phoneNumberVerifiedAt: String? = null

    @SerializedName("has_notification")
    var hasNotification: String? = null

    @SerializedName("wallet_balance")
    var walletBalance:Double =0.0

    // Not in response
    @SerializedName("address")
    var address: String? = ""

    // Not in response
    @SerializedName("latitude")
    var latitude: Double = 0.0

    // Not in response
    @SerializedName("longitude")
    var longitude: Double = 0.0

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        uuid = parcel.readString()
        fullName = parcel.readString()
        email = parcel.readString()
        dialCode = parcel.readString()
        countryCode = parcel.readString()
        phoneNumber = parcel.readString()
        socialType = parcel.readString()
        socialId = parcel.readString()
        avatar = parcel.readString()
        localization = parcel.readString()
        phoneNumberVerifiedAt = parcel.readString()
        hasNotification = parcel.readString()
        walletBalance = parcel.readDouble()
        address = parcel.readString()
        latitude = parcel.readDouble()
        longitude = parcel.readDouble()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(id)
        parcel.writeString(uuid)
        parcel.writeString(fullName)
        parcel.writeString(email)
        parcel.writeString(dialCode)
        parcel.writeString(countryCode)
        parcel.writeString(phoneNumber)
        parcel.writeString(socialType)
        parcel.writeString(socialId)
        parcel.writeString(avatar)
        parcel.writeString(localization)
        parcel.writeString(phoneNumberVerifiedAt)
        parcel.writeString(hasNotification)
        parcel.writeDouble(walletBalance)
        parcel.writeString(address)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
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