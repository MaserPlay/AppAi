package com.maserplay.appai.login

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maserplay.appai.games_web
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginViewModel : ViewModel() {

    var emaillogin: Int = View.GONE
    var totp: Int = View.GONE
    val LoginResponseLiveData: MutableLiveData<Response<LoginResponseClass>> = MutableLiveData()
    val LoginAcceptResponseLiveData: MutableLiveData<Response<LoginVerifyClass>> = MutableLiveData()
    lateinit var cookie: String
    var sum: Int = 0
    val api = Retrofit.Builder()
        .baseUrl(games_web.Base)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(games_web::class.java)

    fun Login(st:LoginClass) {
        viewModelScope.launch {
            val response = api.login(st)
            LoginResponseLiveData.postValue(response)
        }

    }
    fun LoginTotp(st:LoginVerifySendClass) {
        viewModelScope.launch {
            val response = api.totpaccept(st)
            LoginAcceptResponseLiveData.postValue(response)
        }

    }
    fun LoginEmail(st:LoginVerifySendClass) {
        viewModelScope.launch {
            val response = api.emailaccept(st)
            LoginAcceptResponseLiveData.postValue(response)
        }

    }
    fun LoginCheck(st:LoginVerifySendClass) {
        viewModelScope.launch {
            val response = api.checkaccept(st)
            LoginResponseLiveData.postValue(response)
        }

    }

}