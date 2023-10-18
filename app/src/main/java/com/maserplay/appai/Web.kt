package com.maserplay.appai

import com.maserplay.appai.sync.SyncDataClass
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface Web {
    @Headers("Content-Type: application/json")
    @POST("/api/crash")
    fun errorreport(@Body body: ErrorSendClass): Call<ResponseBody>
    @Headers("Content-Type: application/json")
    @POST("/api/syncother")
    suspend fun sync(@Body body: SyncDataClass): Response<SyncDataClass>
    @Headers("Content-Type: application/json")
    @GET("/api/time")
    suspend fun gettime(): Response<String>

}