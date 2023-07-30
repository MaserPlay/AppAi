package com.maserplay.appai

import android.view.View
import androidx.lifecycle.ViewModel
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class LoginViewModel : ViewModel() {

    var emaillogin: Int = View.GONE
    var totp: Int = View.GONE
    var twofactorll: Int = View.GONE
    var loginll: Int = View.VISIBLE
    lateinit var cookie: String
    var sum: Int = 0

    fun request(to: String, st:String): retrofit2.Call<String> {
        return Retrofit.Builder()
            .baseUrl(games_web.Base)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(games_web::class.java).POSTJson(st, to)

    }

}