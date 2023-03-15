package com.nuai.profile.model.api.response

import com.google.gson.annotations.SerializedName
import com.nuai.network.CommonResponse
import com.nuai.profile.model.PaymentInfo

class PaymentListResponse : CommonResponse() {

    @SerializedName("list")
    var paymentList: ArrayList<PaymentInfo>? = null

    @SerializedName("next_offset")
    var nextOffset: Int = 0
}
