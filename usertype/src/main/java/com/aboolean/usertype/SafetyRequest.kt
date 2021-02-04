package com.aboolean.usertype

import com.google.gson.annotations.SerializedName

data class SafetyRequest(
        @SerializedName("data")
        val data: String,
        @SerializedName("package")
        val packageName: String,
        @SerializedName("lat")
        val lat: Double,
        @SerializedName("lng")
        val lng: Double)