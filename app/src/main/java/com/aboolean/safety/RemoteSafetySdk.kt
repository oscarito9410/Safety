package com.aboolean.safety

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.telephony.TelephonyManager
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.google.gson.GsonBuilder

interface SafetySdk {
    suspend fun init(context: Context)
}

class RemoteSafetySdk : SafetySdk {

    override suspend fun init(context: Context) {
        val deviceId = getDeviceId(context)
        deviceId?.let {
            if (!getSharedPreferences(context).getBoolean(ALREADY_SAVED, false)) {
                handleSaveRemoteData(context, it)
            }
        }
    }

    private suspend fun handleSaveRemoteData(context: Context, uuid: String) {
        val dataPayload = GsonBuilder().create().toJson(SafetyRequest(uuid))
        val (request, response, result) =
            Fuel.post("https://www.sosmex.online/add")
                .header("Content-Type" to "application/json").body(dataPayload)
                .awaitStringResponseResult()

        if (response.isSuccessful) {
            getSharedPreferences(context).edit().putBoolean(ALREADY_SAVED, true)
                .apply()
        } else {
            print("Response was not successful the response code is ${response.statusCode}")
        }
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("${context.packageName}.safety", Context.MODE_PRIVATE)
    }

    @SuppressLint("HardwareIds", "MissingPermission")
    private fun getDeviceId(context: Context): String? {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            telephonyManager.deviceId
        } else {
            null
        }
    }

    companion object {
        private const val ALREADY_SAVED = "already_saved"
        private const val UUID = "uuid_user"
        val instance: SafetySdk
            get() {
                return RemoteSafetySdk()
            }
    }
}