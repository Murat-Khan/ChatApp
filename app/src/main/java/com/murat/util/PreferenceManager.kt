package com.murat.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(private val context: Context) {
    private var pref: SharedPreferences = context.getSharedPreferences(
        Constants.KEY_PREFERENCE_NAME,
        Context.MODE_PRIVATE
    )

    fun putBoolean(key: String,value: Boolean) {
      // var editor : SharedPreferences.Editor = pref.edit()
        pref.edit().putBoolean(key, value).apply()

    }
    fun getBoolean(key: String): Boolean {
        return pref.getBoolean(key, false)
    }
    fun putString(key: String,value: String) {
        pref.edit().putString(key, value).apply()
    }
    fun getString(key: String): String? {
        return pref.getString(key, null)
    }
    fun clear(){
        pref.edit().clear().apply()
    }
}