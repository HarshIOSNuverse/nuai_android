package com.checkmyself.home.model

import android.os.Parcel
import android.os.Parcelable

class ResultWrapper() : Parcelable {

    var resultCategoryName: String = ""

    var readingList: ArrayList<Reading>? = null

    constructor(parcel: Parcel) : this() {
        resultCategoryName = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(resultCategoryName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResultWrapper> {
        override fun createFromParcel(parcel: Parcel): ResultWrapper {
            return ResultWrapper(parcel)
        }

        override fun newArray(size: Int): Array<ResultWrapper?> {
            return arrayOfNulls(size)
        }
    }

}

