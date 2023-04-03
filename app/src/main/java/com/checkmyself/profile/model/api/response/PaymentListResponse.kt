package com.checkmyself.profile.model.api.response

import com.checkmyself.network.CommonResponse
import com.checkmyself.profile.model.MyPlan
import com.google.gson.annotations.SerializedName

class PaymentListResponse : CommonResponse() {

    @SerializedName("list")
    var paymentList: ArrayList<MyPlan>? = null

    @SerializedName("next_offset")
    var nextOffset: Int = 0
}
