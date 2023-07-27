package com.maserplay.appai

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModel
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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData

class LoginViewModel : ViewModel() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var totp: EditText
    private lateinit var emailaccept: Button
    val showerrorrequest: MutableLiveData<Object> = MutableLiveData()
    private lateinit var loginbutton: Button
    private lateinit var cookie: String
    private var sum: Int = 0
    fun onCreate(
        emailn: EditText,
        passwordn: EditText,
        totpn: EditText,
        emailacceptn: Button,
        loginbuttonn: Button
    ) {
        email = emailn
        password = passwordn
        totp = totpn
        emailaccept = emailacceptn
        loginbutton = loginbuttonn
    this.totp.addTextChangedListener {
        totpaccept()
    }
    this.loginbutton.setOnClickListener {
        this.loginbutton.isEnabled = false
        val st =
            "{\"email\": \"" + this.email.text + "\", \"password\":\"" + this.password.text + "\", \"App\": \"" + resources.getString(
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

                this@LoginViewModel.loginbutton.isEnabled = true
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
                        this@LoginViewModel.loginbutton.isEnabled = true
                        throw IOException(
                            "Запрос к серверу не был успешен:" +
                                    " ${response.code} ${response.message}"
                        )
                    }
                    val resp: String = response.body.string()
                    val json = JSONObject(resp)
                    runOnUiThread {
                        if (json["email"] == false) {
                            this@LoginViewModel.email.error = resources.getString(R.string.login_error_email)
                        }
                        if (json["password"] == false) {
                            this@LoginViewModel.password.error = resources.getString(R.string.login_error_password)
                            this@LoginViewModel.loginbutton.isEnabled = true
                            return@runOnUiThread
                        } else {
                            if (json["hasaccept"] as Boolean) {
                                cookie = json["acceptcookie"].toString()
                                findViewById<ScrollView>(R.id.twofact).visibility = View.VISIBLE
                                findViewById<ScrollView>(R.id.LoginLayout).visibility =
                                    View.GONE
                                this@LoginViewModel.loginbutton.isEnabled = true
                                val accept = JSONObject(json["accept"].toString())
                                if (accept["totp"] as Boolean) {
                                    findViewById<TextView>(R.id.textView6).visibility =
                                        View.VISIBLE
                                    findViewById<EditText>(R.id.Totp).visibility = View.VISIBLE
                                    sum--
                                }
                                if (accept["emailaccept"] as Boolean) {
                                    findViewById<TextView>(R.id.textView7).visibility =
                                        View.VISIBLE
                                    findViewById<Button>(R.id.checkmail).visibility =
                                        View.VISIBLE
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

fun checksum() {
    if (sum >= 0) {
        val st = "{\"acceptcookie\": \"$cookie\"}"
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
        val st = "{\"acceptcookie\": \"$cookie\", \"code\":  " + totp.text + " }"
        Log.i("TAGG", st.toString())
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
                            sum++
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
    val st = "{\"acceptcookie\": \"$cookie\"}"
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
                        sum++
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
    sum = 0
    totp.setText("")
    findViewById<TextView>(R.id.textView8).visibility = View.GONE
    findViewById<ScrollView>(R.id.LoginLayout).visibility = View.VISIBLE
}
}

}