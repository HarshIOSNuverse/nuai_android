package com.checkmyself.history.model.api.response

import com.google.gson.annotations.SerializedName
import com.checkmyself.network.CommonResponse

class SendScanResponse : CommonResponse() {

    @SerializedName("id")
    var id: Long = 0
}