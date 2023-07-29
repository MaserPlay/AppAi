package com.maserplay.appai

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.maserplay.AppAi.R
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
    private lateinit var model: LoginViewModel
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var totp: EditText
    private lateinit var emailaccept: Button
    private val PREFSFILE = "Main"
    private val PREFNAME = "cookie"
    private lateinit var prefEditor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.Email)
        emailaccept = findViewById(R.id.checkmail)
        totp = findViewById(R.id.Totp)
        password = findViewById(R.id.Password)
        val loginbutton: Button = findViewById(R.id.login)
        model = ViewModelProvider(this)[LoginViewModel::class.java]

        totp.addTextChangedListener {
            totpaccept()
        }
        loginbutton.setOnClickListener {
            loginbutton.isEnabled = false
            val st =
                "{\"email\": \"" + email.text + "\", \"password\":\"" + password.text + "\", \"App\": \"" + resources.getString(
                    R.string.app_name
                ) + "\"}"
            val postBody: RequestBody = st.toRequestBody("application/json".toMediaType())
            OkHttpClient().newCall(
                Request.Builder().post(postBody).url("https://games.m2023.ru/accountlogin").build()
            ).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.request_error),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    loginbutton.isEnabled = true
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            runOnUiThread {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.request_error),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            loginbutton.isEnabled = true
                            throw IOException(
                                "Запрос к серверу не был успешен:" +
                                        " ${response.code} ${response.message}"
                            )
                        }
                        val resp: String = response.body.string()
                        val json = JSONObject(resp)
                        runOnUiThread {
                            if (json["email"] == false) {
                                email.error = resources.getString(R.string.login_error_email)
                            }
                            if (json["password"] == false) {
                                password.error = resources.getString(R.string.login_error_password)
                                loginbutton.isEnabled = true
                                return@runOnUiThread
                            } else {
                                if (json["hasaccept"] as Boolean) {
                                    model.cookie = json["acceptcookie"].toString()
                                    findViewById<ScrollView>(R.id.twofact).visibility = View.VISIBLE
                                    findViewById<ScrollView>(R.id.LoginLayout).visibility =
                                        View.GONE
                                    loginbutton.isEnabled = true
                                    val accept = JSONObject(json["accept"].toString())
                                    if (accept["totp"] as Boolean) {
                                        findViewById<TextView>(R.id.textView6).visibility =
                                            View.VISIBLE
                                        findViewById<EditText>(R.id.Totp).visibility = View.VISIBLE
                                        model.sum--
                                    }
                                    if (accept["emailaccept"] as Boolean) {
                                        findViewById<TextView>(R.id.textView7).visibility =
                                            View.VISIBLE
                                        findViewById<Button>(R.id.checkmail).visibility =
                                            View.VISIBLE
                                        model.sum--
                                    }
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    fun checksum() {
        if (model.sum >= 0) {
            val st = "{\"acceptcookie\": \""  + model.cookie + "\"}"
            val postBody: RequestBody = st.toRequestBody("application/json".toMediaType())
            OkHttpClient().newCall(
                Request.Builder().post(postBody)
                    .url("https://games.m2023.ru/accountlogin/acceptlogin/email").build()
            ).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.request_error),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            runOnUiThread {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.request_error),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            throw IOException(
                                "Запрос к серверу не был успешен:" +
                                        " ${response.code} ${response.message}"
                            )
                        }
                        val resp: String = response.body.string()
                        val json = JSONObject(resp)
                        runOnUiThread {
                            if (json["verify"] as Boolean) {
                                findViewById<Button>(R.id.logintwofac).isEnabled = true
                            }
                        }
                    }
                }
            })
        }
    }

    fun totpaccept() {
        if (totp.text.length == 6) {
            val st = "{ \"acceptcookie\": \"" + model.cookie + "\", \"code\": " + totp.text + " }"
            Log.i("TAGG", st)
            val postBody: RequestBody = st.toRequestBody("application/json".toMediaType())
            OkHttpClient().newCall(
                Request.Builder().post(postBody)
                    .url("https://games.m2023.ru/accountlogin/acceptlogin/totp").build()
            ).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.request_error),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            runOnUiThread {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.request_error),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            Log.i("TAGG", response.code.toString())
                            throw IOException(
                                "Запрос к серверу не был успешен:" +
                                        " ${response.code} ${response.message}"
                            )
                        }
                        for ((name, value) in response.headers) {
                            println("$name: $value")
                        }
                        val resp: String = response.body.string()
                        val json = JSONObject(resp)
                        runOnUiThread {
                            Log.i("TAGG", json.toString())
                            if (json["verify"] as Boolean) {
                                totp.isEnabled = false
                                model.sum++
                                checksum()
                            } else {
                                totp.error = resources.getString(R.string.login_error_code)
                            }
                        }
                    }
                }
            })
        }
    }

    fun emailaccept(view: View) {
        val st = "{\"acceptcookie\": \"" + model.cookie + "\"}"
        val postBody: RequestBody = st.toRequestBody("application/json".toMediaType())
        OkHttpClient().newCall(
            Request.Builder().post(postBody)
                .url("https://games.m2023.ru/accountlogin/acceptlogin/email").build()
        ).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.request_error),
                        Toast.LENGTH_LONG
                    ).show()
                }

                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.request_error),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        throw IOException(
                            "Запрос к серверу не был успешен:" +
                                    " ${response.code} ${response.message}"
                        )
                    }
                    val resp: String = response.body.string()
                    val json = JSONObject(resp)
                    runOnUiThread {
                        Log.i("TAGG", json["verify"].toString())
                        if (json["verify"] as Boolean) {
                            emailaccept.isEnabled = false
                            findViewById<TextView>(R.id.textView8).visibility = View.GONE
                            model.sum++
                            checksum()
                        } else {
                            findViewById<TextView>(R.id.textView8).visibility = View.VISIBLE
                        }
                    }
                }
            }
        })
    }

    fun back(view: View) {
        findViewById<ScrollView>(R.id.twofact).visibility = View.GONE
        totp.isEnabled = true
        emailaccept.isEnabled = true
        findViewById<TextView>(R.id.textView6).visibility =
            View.GONE
        findViewById<EditText>(R.id.Totp).visibility = View.GONE
        findViewById<TextView>(R.id.textView7).visibility =
            View.GONE
        findViewById<Button>(R.id.checkmail).visibility =
            View.GONE
        model.sum = 0
        totp.setText("")
        findViewById<TextView>(R.id.textView8).visibility = View.GONE
        findViewById<ScrollView>(R.id.LoginLayout).visibility = View.VISIBLE
    }
    fun loginlogin(view: View){
        val st = "{\"acceptcookie\": \"" + model.cookie + "\"}"
        val postBody: RequestBody = st.toRequestBody("application/json".toMediaType())
        OkHttpClient().newCall(
            Request.Builder().post(postBody)
                .url("https://games.m2023.ru/accountlogin/acceptlogin/email").build()
        ).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.request_error),
                        Toast.LENGTH_LONG
                    ).show()
                }

                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.request_error),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        throw IOException(
                            "Запрос к серверу не был успешен:" +
                                    " ${response.code} ${response.message}"
                        )
                    }
                    val resp: String = response.body.string()
                    val json = JSONObject(resp)
                    runOnUiThread {
                        prefEditor = getSharedPreferences(PREFSFILE, MODE_PRIVATE).edit()
                        prefEditor.putString(PREFNAME, json["id"].toString())
                        Log.i("LOGG", json["id"].toString())
                    }
                }
            }
        })

    }
}