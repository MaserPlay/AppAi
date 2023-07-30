package com.maserplay.appai

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface games_web {
    companion object {
         val Base: String
            get() = "https://games.m2023.ru/"
    }
    @Headers("Content-Type: application/json")
    @POST("{method}")
    fun POSTJson(@Body body: String, @Path("method") method: String): Call<String>

    @GET("{method}")
    fun GETJson(@Path("method") method: String): Call<String>

}