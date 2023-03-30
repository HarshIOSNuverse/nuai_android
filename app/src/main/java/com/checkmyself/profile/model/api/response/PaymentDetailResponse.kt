package com.checkmyself.profile.model.api.response

import com.google.gson.annotations.SerializedName
import com.checkmyself.network.CommonResponse
import com.checkmyself.profile.model.PaymentInfo

class PaymentDetailResponse : CommonResponse() {

    @SerializedName("data")
    var data: PaymentInfo? = null
}
