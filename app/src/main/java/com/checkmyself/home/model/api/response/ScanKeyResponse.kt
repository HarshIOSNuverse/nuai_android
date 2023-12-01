package com.checkmyself.home.model.api.response

import com.google.gson.annotations.SerializedName
import com.checkmyself.network.CommonResponse
import com.checkmyself.profile.model.PaymentInfo

class ScanKeyResponse : CommonResponse() {

    @SerializedName("key")
    var key: String? = null

    @SerializedName("original_key")
    var originalKey: String? = null

}
