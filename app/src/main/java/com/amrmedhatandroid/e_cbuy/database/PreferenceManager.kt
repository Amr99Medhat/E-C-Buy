package com.amrmedhatandroid.e_cbuy.database

import android.content.Context
import android.content.SharedPreferences
import com.amrmedhatandroid.e_cbuy.utils.Constants

class PreferenceManager(context: Context) {

    private var sharedPreferences: SharedPreferences? = null

    init {
        sharedPreferences =
            context.getSharedPreferences(Constants.EC_Buy_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun putBoolean(key: String, value: Boolean) {
        val editor: SharedPreferences.Editor? = sharedPreferences?.edit()
        editor?.putBoolean(key, value)
        editor?.apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences!!.getBoolean(key, false)
    }

    fun putString(key: String, value: String) {
        val editor: SharedPreferences.Editor? = sharedPreferences?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    fun getString(key: String): String? {
        return sharedPreferences!!.getString(key, "")
    }

    fun clear() {
        val editor: SharedPreferences.Editor? = sharedPreferences?.edit()
        editor?.clear()
        editor?.apply()
    }
}