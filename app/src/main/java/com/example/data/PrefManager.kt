package com.example.data

import android.content.Context
import android.content.SharedPreferences

object PrefManager {
    private const val PREF_NAME = "n8n_prefs"
    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    var authMode: Int
        get() = prefs?.getInt("authMode", -1) ?: -1
        set(value) = prefs?.edit()?.putInt("authMode", value)?.apply() ?: Unit

    var baseUrl: String
        get() = prefs?.getString("baseUrl", "") ?: ""
        set(value) = prefs?.edit()?.putString("baseUrl", value)?.apply() ?: Unit

    var apiKey: String
        get() = prefs?.getString("apiKey", "") ?: ""
        set(value) = prefs?.edit()?.putString("apiKey", value)?.apply() ?: Unit

    var webhookUrl: String
        get() = prefs?.getString("webhookUrl", "") ?: ""
        set(value) = prefs?.edit()?.putString("webhookUrl", value)?.apply() ?: Unit
        
    fun clear() {
        prefs?.edit()?.clear()?.apply()
    }
}
