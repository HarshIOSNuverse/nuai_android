package com.checkmyself.profile.model

import android.os.Parcel
import android.os.Parcelable
import com.android.billingclient.api.ProductDetails
import com.google.gson.annotations.SerializedName

class SubscriptionPlan() : Parcelable {

    @SerializedName("id")
    var id: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("observedValue")
    var observedValue: String? = null

    @SerializedName("shortDesc")
    var shortDesc: String? = null

    @SerializedName("sku")
    var skuDetailsResult: ProductDetails? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        title = parcel.readString()
        observedValue = parcel.readString()
        shortDesc = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(observedValue)
        parcel.writeString(shortDesc)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SubscriptionPlan> {
        override fun createFromParcel(parcel: Parcel): SubscriptionPlan {
            return SubscriptionPlan(parcel)
        }

        override fun newArray(size: Int): Array<SubscriptionPlan?> {
            return arrayOfNulls(size)
        }
    }

}