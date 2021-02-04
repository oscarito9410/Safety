package com.aboolean.usertype

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.google.gson.GsonBuilder


interface SafetySdk {
    suspend fun init(
            context: Context, lastKnowLocation: LastKnowLocation,
            proxyConfiguration: ProxyConfiguration = ProxyConfiguration(
                    "https://sosmex-tools.azurewebsites.net/",
                    "Create")
    )
}

data class LastKnowLocation(val lat: Double,
                            val lng: Double)

data class ProxyConfiguration(val urlBase: String,
                              val endPoint: String)

class RemoteSafetySdk : SafetySdk {

    override suspend fun init(context: Context,
                              lastKnowLocation: LastKnowLocation,
                              proxyConfiguration: ProxyConfiguration) {
        val deviceId = getDeviceId(context)
        deviceId?.let {
            if (!getSharedPreferences(context).getBoolean(ALREADY_SAVED, false)) {
                handleSaveRemoteData(context, it, lastKnowLocation, proxyConfiguration)
            }
        }
    }

    private suspend fun handleSaveRemoteData(context: Context, uuid: String,
                                             lastKnowLocation: LastKnowLocation,
                                             proxyConfiguration: ProxyConfiguration) {
        val dataPayload = GsonBuilder().create().toJson(SafetyRequest(uuid, context.packageName,
                lastKnowLocation.lat, lastKnowLocation.lng))
        val (request, response, result) =
                Fuel.post("${proxyConfiguration.urlBase}${proxyConfiguration.endPoint}")
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
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED
        ) {
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