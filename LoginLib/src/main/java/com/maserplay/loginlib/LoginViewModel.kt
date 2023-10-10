package com.maserplay.loginlib

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maserplay.loginlib.send_get_classes.LoginClass
import com.maserplay.loginlib.send_get_classes.LoginResponseClass
import com.maserplay.loginlib.send_get_classes.LoginVerifyClass
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginViewModel : ViewModel() {
    val LoginResponseLiveData: MutableLiveData<Response<LoginResponseClass>> = MutableLiveData()
    lateinit var cookie: String
    var sum: Int = 0
    val api = Retrofit.Builder()
        .baseUrl(GlobalVariables.WEB_ADR_FULL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Web::class.java)

    fun Login(st: LoginClass) {
        viewModelScope.launch {
            val response = api.login(st)
            LoginResponseLiveData.postValue(response)
        }

    }
}