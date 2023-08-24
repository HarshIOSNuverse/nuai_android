package com.checkmyself.profile.model.api.request

import com.google.gson.annotations.SerializedName

data class PurchaseRequest constructor(
    @SerializedName("amount") var amount: String? = "",
    @SerializedName("currency") var currency: String? = "",
    @SerializedName("plan_type") val planType: String?,
    @SerializedName("trx_ref_no") val trxRefNo: String?,
    @SerializedName("trx_datetime") val trxDatetime: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("product_id") val productId: String?,
    @SerializedName("purchase_token") val purchaseToken: String?,
    @SerializedName("plan_amount") val planAmount: String?,
)