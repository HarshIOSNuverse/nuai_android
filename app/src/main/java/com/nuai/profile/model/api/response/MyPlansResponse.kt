package com.nuai.profile.model.api.response

import com.google.gson.annotations.SerializedName
import com.nuai.network.CommonResponse
import com.nuai.profile.model.MyPlan

class MyPlansResponse : CommonResponse() {

    @SerializedName("active_plan")
    var activePlan: MyPlan? = null

    @SerializedName("upcoming_plan")
    var upcomingPlan:MyPlan? = null
}