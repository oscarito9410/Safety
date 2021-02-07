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
import java.io.IOException
import java.lang.Exception

interface SafetySdk {
    suspend fun init(context: Context, lastKnowLocation: LastKnowLocation,
                     proxyConfiguration: ProxyConfiguration)
}

data class LastKnowLocation(val lat: Double,
                            val lng: Double)

data class ProxyConfiguration(val urlBase: String,
                              val endPoint: String)

class RemoteSafetySdk : SafetySdk {

    private lateinit var preferencesManagerSafety: PreferencesManagerSafety

    override suspend fun init(context: Context, lastKnowLocation: LastKnowLocation,
                              proxyConfiguration: ProxyConfiguration) {
        preferencesManagerSafety = PreferencesManager(context)
        val deviceId = getDeviceId(context)
        deviceId?.let {
            if (!preferencesManagerSafety.alreadySaved()) {
                handleSaveRemoteData(context, it, lastKnowLocation, proxyConfiguration)
            }
        }
    }

    private suspend fun handleSaveRemoteData(context: Context, uuid: String,
                                             lastKnowLocation: LastKnowLocation,
                                             proxyConfiguration: ProxyConfiguration) {
        val dataPayload = GsonBuilder().create().toJson(SafetyRequest(uuid, context.packageName,
                lastKnowLocation.lat, lastKnowLocation.lng))
        val (_, response, result) =
                Fuel.post("${proxyConfiguration.urlBase}${proxyConfiguration.endPoint}")
                        .header("Content-Type" to "application/json").body(dataPayload)
                        .awaitStringResponseResult()
        when {
            response.isSuccessful -> {
                preferencesManagerSafety.isAlreadySaved(true)
            }
            else -> {
                preferencesManagerSafety.isAlreadySaved(false)
                print("Response was not successful the response code is ${response.statusCode}")
            }
        }
    }

    @SuppressLint("HardwareIds", "MissingPermission")
    private fun getDeviceId(context: Context): String? {
        return try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE)
                        as TelephonyManager
                telephonyManager.deviceId
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        val instance: SafetySdk
            get() {
                return RemoteSafetySdk()
            }
    }
}