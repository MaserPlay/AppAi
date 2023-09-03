package com.maserplay.appai.sync

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maserplay.AppAi.R
import com.maserplay.appai.GlobalVariables
import com.maserplay.appai.Web
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class SyncViewModel : ViewModel() {
    val datetime: MutableLiveData<Response<String>> = MutableLiveData()
    private val api = Retrofit.Builder()
        .baseUrl(GlobalVariables.WEB_ADR_FULL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Web::class.java)

    fun setdatetime(con: Context) {
        viewModelScope.launch {
            val response = api.gettime()
            if (!response.isSuccessful) {
                Toast.makeText(
                    con,
                    con.getString(R.string.request_error),
                    Toast.LENGTH_LONG
                ).show()
            }
            con.getSharedPreferences(
                GlobalVariables.SHAREDPREFERENCES_NAME,
                AppCompatActivity.MODE_PRIVATE
            ).edit()
                .putString("lastupdate", response.body()).apply()
        }
    }

    fun getdatetime() {
        viewModelScope.launch {
            val response = api.gettime()
            datetime.postValue(response)
        }
    }
}