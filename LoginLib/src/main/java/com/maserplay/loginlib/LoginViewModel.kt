package com.maserplay.loginlib

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginViewModel : ViewModel() {
    val LoginResponseLiveData: MutableLiveData<Response<LoginResponseClass>> = MutableLiveData()
    val RegisterResponseLiveData: MutableLiveData<Response<RegisterResponseClass>> = MutableLiveData()
    lateinit var cookie: String

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

    fun Register(st: RegisterClass) {
        viewModelScope.launch {
            val response = api.register(st)
            RegisterResponseLiveData.postValue(response)
        }
    }
}