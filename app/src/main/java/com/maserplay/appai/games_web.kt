package com.maserplay.appai

import com.maserplay.appai.login.LoginClass
import com.maserplay.appai.login.LoginResponseClass
import com.maserplay.appai.login.LoginVerifyClass
import com.maserplay.appai.login.LoginVerifySendClass
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface games_web {
    companion object {
         val Base: String
            get() = "https://games.m2023.ru/"
    }
    @Headers("Content-Type: application/json")
    @POST("/accountlogin")
    suspend fun login(@Body body: LoginClass): Response<LoginResponseClass>
    @Headers("Content-Type: application/json")
    @POST("/accountlogin/acceptlogin/email")
    suspend fun emailaccept(@Body body: LoginVerifySendClass): Response<LoginVerifyClass>
    @Headers("Content-Type: application/json")
    @POST("/accountlogin/acceptlogin/totp")
    suspend fun totpaccept(@Body body: LoginVerifySendClass): Response<LoginVerifyClass>
    @Headers("Content-Type: application/json")
    @POST("/accountlogin/acceptlogin/check")
    suspend fun checkaccept(@Body body: LoginVerifySendClass): Response<LoginResponseClass>
    @Headers("Content-Type: application/json")
    @GET("/time")
    suspend fun gettime(): Response<String>

}