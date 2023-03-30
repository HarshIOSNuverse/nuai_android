package com.checkmyself.profile.model.api.response

import com.google.gson.annotations.SerializedName
import com.checkmyself.network.CommonResponse
import com.checkmyself.profile.model.PaymentInfo

class PaymentListResponse : CommonResponse() {

    @SerializedName("list")
    var paymentList: ArrayList<PaymentInfo>? = null

    @SerializedName("next_offset")
    var nextOffset: Int = 0
}
