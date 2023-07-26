package com.maserplay.AppAi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class Login_games_m2023_ru_Activity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.Email)
        password = findViewById(R.id.Password)
        findViewById<Button>(R.id.login).setOnClickListener {
            val st = "{\"email\": \"" + email.text +"\", \"password\":\"" + password.text + "\", \"App\": \"" + resources.getString(R.string.app_name) + "\"}"
            val postBody: RequestBody = st.toRequestBody("application/json".toMediaType())
            OkHttpClient().newCall(
                Request.Builder().post(postBody).url("https://games.m2023.ru/accountlogin").build()
            ).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            throw IOException("Запрос к серверу не был успешен:" +
                                    " ${response.code} ${response.message}")
                        }
                        // пример получения всех заголовков ответа
                        for ((name, value) in response.headers) {
                            println("$name: $value")
                        }
                        val resp: String = response.body.string()
                        val json = JSONObject(resp)
                        runOnUiThread {
                            if (json["email"] == false) {email.error = resources.getString(R.string.login_error_email)}
                            if (json["password"] == false) {password.error = resources.getString(R.string.login_error_password)}
                        }
                    }
                }
            })
        }
    }
}