package com.maserplay.AppAi

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
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
import kotlin.properties.Delegates

class Login_games_m2023_ru_Activity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var totp: EditText
    private lateinit var emailaccept: Button
    private lateinit var cookie: String
    private var sum by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.Email)
        emailaccept = findViewById(R.id.checkmail)
        totp = findViewById(R.id.Totp)
        password = findViewById(R.id.Password)
        val loginbutton: Button = findViewById(R.id.login)
        loginbutton.setOnClickListener {
            loginbutton.isEnabled = false
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
                        for ((name, value) in response.headers) {
                            println("$name: $value")
                        }
                        val resp: String = response.body.string()
                        val json = JSONObject(resp)
                        runOnUiThread {
                            if (json["email"] == false) {email.error = resources.getString(R.string.login_error_email)}
                            if (json["password"] == false) {password.error = resources.getString(R.string.login_error_password)
                                loginbutton.isEnabled = true
                                return@runOnUiThread } else {
                            if (json["hasaccept"] as Boolean) {
                                cookie = json["acceptcookie"].toString()
                                findViewById<ScrollView>(R.id.twofact).visibility = View.VISIBLE
                                findViewById<ScrollView>(R.id.LoginLayout).visibility = View.GONE
                            val accept = JSONObject(json["accept"].toString())
                                Log.i("Log",  accept.toString())
                                if (accept["totp"] as Boolean) {
                                    findViewById<TextView>(R.id.textView6).visibility = View.VISIBLE
                                    findViewById<EditText>(R.id.Totp).visibility = View.VISIBLE
                                    sum--
                                }
                                if (accept["emailaccept"] as Boolean) {
                                    findViewById<TextView>(R.id.textView7).visibility = View.VISIBLE
                                    findViewById<Button>(R.id.checkmail).visibility = View.VISIBLE
                                    sum--
                                }
                            }
                            }
                        }
                    }
                }
            })
        }
    }
    fun checksum(){if (sum >= 0) {findViewById<Button>(R.id.logintwofac).isEnabled = true}}
    fun emailaccept(){ checksum() }
    fun totpaccept(){if (totp.text.length == 6) {
        val st = "{\"cookie\": \"" + email.text +"\", \"password\":\"" + password.text + "\", \"App\": \"" + resources.getString(R.string.app_name) + "\"}"
        val postBody: RequestBody = st.toRequestBody("application/json".toMediaType())
        OkHttpClient().newCall(
            Request.Builder().post(postBody).url("https://games.m2023.ru/accountlogin/sendmail").build()
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
                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }
                    val resp: String = response.body.string()
                    val json = JSONObject(resp)
                    runOnUiThread {
                        if (json["verify"] as Boolean) {emailaccept.isEnabled = true
                        sum++
                            checksum()} else {findViewById<TextView>(R.id.textView8).visibility = View.VISIBLE}
                    }
                }
            }
        })
    }}
    fun back(){
        findViewById<ScrollView>(R.id.twofact).visibility = View.GONE
        findViewById<ScrollView>(R.id.LoginLayout).visibility = View.VISIBLE}
}