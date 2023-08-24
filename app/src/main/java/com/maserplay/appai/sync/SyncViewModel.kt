package com.maserplay.appai.sync

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    fun getdatetime() {
        viewModelScope.launch {
            val response = api.gettime()
            datetime.postValue(response)
        } }
}