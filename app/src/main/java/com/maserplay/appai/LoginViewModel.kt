package com.maserplay.appai

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class LoginViewModel : ViewModel() {
    val bool: MutableLiveData<Int> = MutableLiveData()
    lateinit var cookie: String
    var sum: Int = 0

    fun request(to: String, st:String, i: Int) {
        val postBody: RequestBody = st.toRequestBody("application/json".toMediaType())
        OkHttpClient().newCall(
            Request.Builder().post(postBody)
                .url("https://games.m2023.ru$to").build()
        ).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                bool.postValue(i)
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    bool.postValue(i)
                }
            }
        })

    }

}