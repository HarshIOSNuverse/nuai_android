package com.checkmyself.profile.model.api.response

import com.google.gson.annotations.SerializedName
import com.checkmyself.network.CommonResponse

class PurchaseResponse : CommonResponse() {

    @SerializedName("id")
    var id: String? = null

    @SerializedName("trx_no")
    var trxNo: String? = null
}
