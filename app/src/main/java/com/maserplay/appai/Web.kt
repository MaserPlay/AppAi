package com.maserplay.appai

import com.maserplay.appai.login.send_get_classes.LoginClass
import com.maserplay.appai.login.send_get_classes.LoginResponseClass
import com.maserplay.appai.login.send_get_classes.LoginVerifyClass
import com.maserplay.appai.login.send_get_classes.LoginVerifySendClass
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
    @POST("/test/mirrorjson")
    suspend fun sync(@Body body: SyncDataClass): Response<SyncDataClass>
    @Headers("Content-Type: application/json")
    @POST("/api/accountlogin")
    suspend fun login(@Body body: LoginClass): Response<LoginResponseClass>
    @Headers("Content-Type: application/json")
    @POST("/api/accountlogin/acceptlogin/email")
    suspend fun emailaccept(@Body body: LoginVerifySendClass): Response<LoginVerifyClass>
    @Headers("Content-Type: application/json")
    @POST("/api/accountlogin/acceptlogin/totp")
    suspend fun totpaccept(@Body body: LoginVerifySendClass): Response<LoginVerifyClass>
    @Headers("Content-Type: application/json")
    @POST("/api/accountlogin/acceptlogin/check")
    suspend fun checkaccept(@Body body: LoginVerifySendClass): Response<LoginResponseClass>
    @Headers("Content-Type: application/json")
    @GET("/api/time")
    suspend fun gettime(): Response<String>

}