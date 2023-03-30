package com.checkmyself.profile.model.api.response

import com.google.gson.annotations.SerializedName
import com.checkmyself.network.CommonResponse
import com.checkmyself.profile.model.MyPlan

class MyPlansResponse : CommonResponse() {

    @SerializedName("active_plan")
    var activePlan: MyPlan? = null

    @SerializedName("upcoming_plan")
    var upcomingPlan:MyPlan? = null
}