package com.checkmyself.profile.model.api.request

import com.google.gson.annotations.SerializedName

data class PurchaseRequest constructor(
    @SerializedName("subscription_id") val subscriptionId: String?,
    @SerializedName("amount") var amount: String? = "",
    @SerializedName("currency") var currency: String? = "",
    @SerializedName("order_object") var orderObject: String? = "",
    @SerializedName("orderId") var orderId: String? = "",
    @SerializedName("package_name") var packageName: String? = "",
    @SerializedName("purchase_token") var purchaseToken: String? = ""
)