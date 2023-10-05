package com.maserplay.appai.login

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maserplay.appai.GlobalVariables
import com.maserplay.appai.login.send_get_classes.LoginClass
import com.maserplay.appai.login.send_get_classes.LoginResponseClass
import com.maserplay.appai.login.send_get_classes.LoginVerifyClass
import com.maserplay.appai.login.send_get_classes.LoginVerifySendClass
import com.maserplay.appai.Web
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
    fun LoginTotp(st: LoginVerifySendClass) {
        viewModelScope.launch {
            val response = api.totpaccept(st)
            LoginAcceptResponseLiveData.postValue(response)
        }

    }
    fun LoginEmail(st: LoginVerifySendClass) {
        viewModelScope.launch {
            val response = api.emailaccept(st)
            LoginAcceptResponseLiveData.postValue(response)
        }

    }
    fun LoginCheck(st: LoginVerifySendClass) {
        viewModelScope.launch {
            val response = api.checkaccept(st)
            LoginResponseLiveData.postValue(response)
        }
    }
}