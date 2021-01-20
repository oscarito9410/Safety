package com.aboolean.safety

import com.google.gson.annotations.SerializedName

data class SafetyRequest(@SerializedName("param1")
                         val uuid: String)