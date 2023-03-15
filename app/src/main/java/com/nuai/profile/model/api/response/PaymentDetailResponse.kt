package com.nuai.profile.model.api.response

import com.google.gson.annotations.SerializedName
import com.nuai.network.CommonResponse
import com.nuai.profile.model.PaymentInfo

class PaymentDetailResponse : CommonResponse() {

    @SerializedName("data")
    var data: PaymentInfo? = null
}
