package com.maserplay.appai

import android.content.SharedPreferences
import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class ShListener : SharedPreferences.OnSharedPreferenceChangeListener {
    private val api = Retrofit.Builder()
        .baseUrl(GlobalVariables.WEB_ADR_FULL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Web::class.java)
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.i(GlobalVariables.LOGTAG_SYNC, "getdatetime")
        val df: DateFormat = DateFormat.getTimeInstance()
        df.setTimeZone(TimeZone.getTimeZone("gmt"))
        val gmtTime: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(Date())
        sharedPreferences?.edit()?.putString("lastupdate", gmtTime)?.apply()
    }
}