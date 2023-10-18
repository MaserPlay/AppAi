package com.maserplay.loginlib

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface Web {
    @Headers("Content-Type: application/json")
    @POST("/api/auth/login")
    suspend fun login(@Body body: LoginClass): Response<LoginResponseClass>
    @Headers("content-Type: application/json")
    @POST("/api/auth/registration")
    suspend fun register(@Body body: RegisterClass): Response<RegisterResponseClass>

}