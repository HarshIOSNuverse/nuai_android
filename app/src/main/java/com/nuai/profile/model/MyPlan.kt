package com.nuai.profile.model

import com.google.gson.annotations.SerializedName
import com.nuai.network.CommonResponse

class MyPlan : CommonResponse() {

    @SerializedName("active_plan")
    var activePlan:MyPlan? = null

    @SerializedName("upcoming_plan")
    var upcomingPlan:MyPlan? = null
}