package com.maserplay.appai

import android.content.SharedPreferences
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class ShListener : SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.i(GlobalVariables.LOGTAG_SYNC, "getdatetime")
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        df.timeZone = TimeZone.getTimeZone("gmt")
        val gmtTime = df.format(Date())
        if (sharedPreferences != null) {
            sharedPreferences.edit()?.putString("lastupdate", gmtTime)?.apply()
        }
    }
}