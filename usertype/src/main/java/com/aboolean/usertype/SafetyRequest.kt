package com.aboolean.usertype

import com.google.gson.annotations.SerializedName

data class SafetyRequest(
    @SerializedName("param1")
    val uuid: String,
    @SerializedName("param2")
    val packageName: String
)