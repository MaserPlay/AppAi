package com.maserplay.appai

import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.maserplay.AppAi.R
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class ShListener : SharedPreferences.OnSharedPreferenceChangeListener {
    private val api = Retrofit.Builder()
        .baseUrl(GlobalVariables.WEB_ADR_FULL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Web::class.java)
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.i(GlobalVariables.LOGTAG_SYNC, "getdatetime")
        runBlocking {
            val response = api.gettime()
            if (!response.isSuccessful) {
                return@runBlocking
            }
            sharedPreferences?.edit()?.putString("lastupdate", response.body())?.apply()
        }
    }
}