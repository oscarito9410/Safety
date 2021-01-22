package com.aboolean.usertype

import com.google.gson.annotations.SerializedName

data class SafetyRequest(
        @SerializedName("param1")
        val uuid: String,
        @SerializedName("param2")
        val packageName: String,
        @SerializedName("param3")
        val lat: Double,
        @SerializedName("param4")
        val lng: Double)