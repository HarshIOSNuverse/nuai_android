package com.checkmyself.profile.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class PaymentInfo() : Parcelable {

    @SerializedName("id")
    var id: Long = 0

    @SerializedName("uuid")
    var uuid: String? = null

    @SerializedName("user_id")
    var userId: Long = 0

    @SerializedName("plan_type")
    var planType: String? = null

    @SerializedName("start_date")
    var startDate: String? = null

    @SerializedName("end_date")
    var endDate: String? = null

    @SerializedName("trx_no")
    var trxNo: String? = null

    @SerializedName("trx_ref_no")
    var trxRefNo: String? = null

    @SerializedName("trx_amount")
    var trxAmount: Double = 0.0

    @SerializedName("trx_currency")
    var trxCurrency: String? = null

    @SerializedName("trx_datetime")
    var trxDatetime: String? = null

    @SerializedName("trx_status")
    var trxStatus: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("user")
    var user: User? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        uuid = parcel.readString()
        userId = parcel.readLong()
        planType = parcel.readString()
        startDate = parcel.readString()
        endDate = parcel.readString()
        trxNo = parcel.readString()
        trxRefNo = parcel.readString()
        trxAmount = parcel.readDouble()
        trxCurrency = parcel.readString()
        trxDatetime = parcel.readString()
        trxStatus = parcel.readString()
        status = parcel.readString()
        user = parcel.readParcelable(User::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(uuid)
        parcel.writeLong(userId)
        parcel.writeString(planType)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeString(trxNo)
        parcel.writeString(trxRefNo)
        parcel.writeDouble(trxAmount)
        parcel.writeString(trxCurrency)
        parcel.writeString(trxDatetime)
        parcel.writeString(trxStatus)
        parcel.writeString(status)
        parcel.writeParcelable(user, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentInfo> {
        override fun createFromParcel(parcel: Parcel): PaymentInfo {
            return PaymentInfo(parcel)
        }

        override fun newArray(size: Int): Array<PaymentInfo?> {
            return arrayOfNulls(size)
        }
    }

}

