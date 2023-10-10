package com.maserplay.loginlib

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface Web {
    @Headers("Content-Type: application/json")
    @POST("/api/accountlogin")
    suspend fun login(@Body body: com.maserplay.loginlib.send_get_classes.LoginClass): Response<com.maserplay.loginlib.send_get_classes.LoginResponseClass>
    @Headers("Content-Type: application/json")
    @GET("/api/time")
    suspend fun gettime(): Response<String>

}