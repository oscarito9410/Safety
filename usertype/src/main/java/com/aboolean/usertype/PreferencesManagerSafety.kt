package com.aboolean.usertype

import android.content.Context
import android.content.SharedPreferences

interface PreferencesManagerSafety {
    fun alreadySaved(): Boolean
    fun isAlreadySaved(saved: Boolean)
}

internal class PreferencesManager(private val context: Context) : PreferencesManagerSafety {

    override fun alreadySaved(): Boolean {
        return getSharedPreferences(context).getBoolean(ALREADY_SAVED, false)
    }

    override fun isAlreadySaved(saved: Boolean) {
        getSharedPreferences(context).edit().putBoolean(ALREADY_SAVED, saved)
            .apply()
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("${context.packageName}.safety", Context.MODE_PRIVATE)
    }

    companion object {
        private const val ALREADY_SAVED = "already_saved"
    }
}