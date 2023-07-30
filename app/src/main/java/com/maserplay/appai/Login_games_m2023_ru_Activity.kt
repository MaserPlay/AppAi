package com.maserplay.appai

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
import org.json.JSONObject

class Login_games_m2023_ru_Activity : AppCompatActivity() {
    private lateinit var model: LoginViewModel
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var totp: EditText
    private lateinit var emailaccept: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.Email)
        emailaccept = findViewById(R.id.checkmail)
        totp = findViewById(R.id.Totp)
        password = findViewById(R.id.Password)
        val loginbutton: Button = findViewById(R.id.login)
        model = ViewModelProvider(this)[LoginViewModel::class.java]

        findViewById<ScrollView>(R.id.twofact).visibility = model.twofactorll
        findViewById<ScrollView>(R.id.LoginLayout).visibility = model.loginll
        findViewById<TextView>(R.id.textView6).visibility = model.totp
        totp.visibility = model.totp
        findViewById<TextView>(R.id.textView7).visibility = model.emaillogin
        emailaccept.visibility = model.emaillogin

        totp.addTextChangedListener {
            totpaccept()
        }
        loginbutton.setOnClickListener {
            loginbutton.isEnabled = false
            model.request("/accountlogin", "{\"email\": \"" + email.text + "\", \"password\":\"" + password.text + "\", \"App\": \"" + resources.getString(R.string.app_name) + "\"}").enqueue(object :
                retrofit2.Callback<String> {
                override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.request_error),
                            Toast.LENGTH_LONG
                        ).show()


                    loginbutton.isEnabled = true
                }

                override fun onResponse(
                    call: retrofit2.Call<String>,
                    response: retrofit2.Response<String>
                ) {
                        if (!response.isSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.request_error),
                                    Toast.LENGTH_LONG
                                ).show()

                            loginbutton.isEnabled = true
                        } else {
                            val resp: String = response.body().toString()
                            val json = JSONObject(resp)
                            runOnUiThread {
                                if (json["email"] == false) {
                                    email.error = resources.getString(R.string.login_error_email)
                                }
                                if (json["password"] == false) {
                                    password.error =
                                        resources.getString(R.string.login_error_password)
                                    loginbutton.isEnabled = true
                                    return@runOnUiThread
                                } else {
                                    if (json["hasaccept"] as Boolean) {
                                        model.cookie = json["acceptcookie"].toString()
                                        model.twofactorll = View.VISIBLE
                                        findViewById<ScrollView>(R.id.twofact).visibility = model.twofactorll
                                        model.loginll = View.GONE
                                        findViewById<ScrollView>(R.id.LoginLayout).visibility =
                                            model.loginll
                                        loginbutton.isEnabled = true
                                        val accept = JSONObject(json["accept"].toString())
                                        if (accept["totp"] as Boolean) {
                                            model.totp = View.VISIBLE
                                            findViewById<TextView>(R.id.textView6).visibility =
                                                model.totp
                                            findViewById<EditText>(R.id.Totp).visibility =
                                                model.totp
                                            model.sum--
                                        }
                                        if (accept["emailaccept"] as Boolean) {
                                            model.emaillogin = View.VISIBLE
                                            findViewById<TextView>(R.id.textView7).visibility =
                                                model.emaillogin
                                            findViewById<Button>(R.id.checkmail).visibility =
                                                model.emaillogin
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

            val jsonObject = JSONObject()
                .put("acceptcookie", model.cookie)
            model.request("/accountlogin/acceptlogin/email", jsonObject.toString()).enqueue(object :
                    retrofit2.Callback<String> {
                override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.request_error),
                            Toast.LENGTH_LONG
                        ).show()

                }

                override fun onResponse(
                    call: retrofit2.Call<String>,
                    response: retrofit2.Response<String>
                ) {
                        if (!response.isSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.request_error),
                                    Toast.LENGTH_LONG
                                ).show()

                        }
                        val resp: String = response.body().toString()
                        val json = JSONObject(resp)
                            if (json["verify"] as Boolean) {
                                findViewById<Button>(R.id.logintwofac).isEnabled = true
                            }


                }
            })
        }
    }

    fun totpaccept() {
        if (totp.text.length == 6) {
            val jsonObject = JSONObject()
                .put("acceptcookie", model.cookie)
                .put("code", totp.text)
            model.request("/accountlogin/acceptlogin/totp", jsonObject.toString()).enqueue(object :
                    retrofit2.Callback<String> {
                    override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.request_error),
                                Toast.LENGTH_LONG
                            ).show()

                    }

                    override fun onResponse(call: retrofit2.Call<String>, response: retrofit2.Response<String>) {
                            if (!response.isSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        getString(R.string.request_error),
                                        Toast.LENGTH_LONG
                                    ).show()

                            } else {
                                val resp: String = response.body().toString()
                                val json = JSONObject(resp)
                                if (json["verify"] as Boolean) {
                                    totp.isEnabled = false
                                    model.sum++
                                    checksum()
                                } else {
                                    totp.error = resources.getString(R.string.login_error_code)
                                }
                            }


                    }
                })
            }
        }


    fun emailaccept(view: View) {
        model.request("/accountlogin/acceptlogin/email", "{\"acceptcookie\": \"" + model.cookie + "\"}").enqueue(object : retrofit2.Callback<String> {
            override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.request_error),
                        Toast.LENGTH_LONG
                    ).show()

            }

            override fun onResponse(
                call: retrofit2.Call<String>,
                response: retrofit2.Response<String>
            ) {
                    if (!response.isSuccessful) {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.request_error),
                                Toast.LENGTH_LONG
                            ).show()

                    }
                    val resp: String = response.body().toString()
                    val json = JSONObject(resp)
                        if (json["verify"] as Boolean) {
                            emailaccept.isEnabled = false
                            findViewById<TextView>(R.id.textView8).visibility = View.GONE
                            model.sum++
                            checksum()
                        } else {
                            findViewById<TextView>(R.id.textView8).visibility = View.VISIBLE
                        }


            }
        })
    }

    fun back(view: View) {
        findViewById<ScrollView>(R.id.twofact).visibility = View.GONE
        totp.isEnabled = true
        emailaccept.isEnabled = true
        model.emaillogin = View.GONE
        model.totp = View.GONE
        model.twofactorll = View.GONE
        model.loginll = View.VISIBLE
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
        model.request("/accountlogin/acceptlogin/email", "{\"acceptcookie\": \"" + model.cookie + "\"}").enqueue(object : retrofit2.Callback<String> {
            override fun onFailure(call: retrofit2.Call<String>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.request_error),
                        Toast.LENGTH_LONG
                    ).show()

            }

            override fun onResponse(
                call: retrofit2.Call<String>,
                response: retrofit2.Response<String>
            ) {
                    Log.i("TAGG", response.body().toString())
                    if (!response.isSuccessful) {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.request_error),
                                Toast.LENGTH_LONG
                            ).show()
                    } else {
                    val resp: String = response.body().toString()
                    val json = JSONObject(resp)
                    getSharedPreferences("Main", MODE_PRIVATE).edit().putString("cookie", json["id"].toString()).apply()}
            }
        })

    }}