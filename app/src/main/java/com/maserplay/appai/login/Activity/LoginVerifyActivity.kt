package com.maserplay.appai.login.Activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.maserplay.appai.login.LoginViewModel
import com.maserplay.AppAi.R
import com.maserplay.appai.login.send_get_classes.LoginVerifySendClass
import org.json.JSONObject

class LoginVerifyActivity : AppCompatActivity() {
    private lateinit var model: LoginViewModel
    private lateinit var totp: EditText
    private lateinit var emailaccept: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_verify)
        emailaccept = findViewById(R.id.checkmail)
        totp = findViewById(R.id.Totp)
        model = ViewModelProvider(this)[LoginViewModel::class.java]
        findViewById<TextView>(R.id.textView6).visibility = model.totp
        totp.visibility = model.totp
        findViewById<TextView>(R.id.textView7).visibility = model.emaillogin
        emailaccept.visibility = model.emaillogin

        totp.addTextChangedListener {
            totpaccept()
        }
    }

    fun checksum() {
        if (model.sum >= 0) {
            findViewById<Button>(R.id.logintwofac).isEnabled = true
        }
    }

    fun totpaccept() {
        if (totp.text.length == 6) {
            model.LoginAcceptResponseLiveData.observe(this) {
                if (!it.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.request_error),
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    val resp: String = it.body().toString()
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
        }
        model.LoginTotp(LoginVerifySendClass(model.cookie, totp.text.toString().toInt()))
    }


    fun emailaccept(view: View) {
        model.LoginAcceptResponseLiveData.observe(this) {
            if (!it.isSuccessful) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.request_error),
                    Toast.LENGTH_LONG
                ).show()

            }
            val resp: String = it.body().toString()
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
        model.LoginEmail(LoginVerifySendClass(model.cookie))
    }

    fun loginlogin(view: View) {
        model.LoginResponseLiveData.observe(this) {
            if (!it.isSuccessful) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.request_error),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val resp: String = it.body().toString()
                val json = JSONObject(resp)
                getSharedPreferences("Main", MODE_PRIVATE).edit()
                    .putString("cookie", json["id"].toString()).putString("nickname", json["id"].toString()).apply()
            }
        }

        model.LoginCheck(LoginVerifySendClass(model.cookie))

    }
}