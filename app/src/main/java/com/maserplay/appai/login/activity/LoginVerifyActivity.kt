package com.maserplay.appai.login.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle
import android.util.Log
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

class LoginVerifyActivity : AppCompatActivity() {
    private lateinit var model: LoginViewModel
    private lateinit var totp: EditText
    private lateinit var emailaccept: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_verify)
        if (AccountManager.get(this).accounts.isNotEmpty()) {
            Toast.makeText(this, getString(R.string.error_toomany), Toast.LENGTH_LONG).show()
            finish()}
        emailaccept = findViewById(R.id.checkmail)
        totp = findViewById(R.id.Totp)
        model = ViewModelProvider(this)[LoginViewModel::class.java]
        val b: Bundle = intent.extras!!
        model.cookie = b.getString("cookie").toString()
        model.totp = b.getInt("totp")
        model.emaillogin = b.getInt("email")
        findViewById<TextView>(R.id.textView6).visibility = model.totp
        totp.visibility = model.totp
        findViewById<TextView>(R.id.textView7).visibility = model.emaillogin
        emailaccept.visibility = model.emaillogin

        totp.addTextChangedListener {
            totpaccept()
        }
    }

    private fun checksum() {
        if (model.sum >= 0) {
            findViewById<Button>(R.id.logintwofac).isEnabled = true
        }
    }

    private fun totpaccept() {
        if (totp.text.length == 6) {
            model.LoginAcceptResponseLiveData.observe(this) {
                if (!it.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.request_error),
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    val resp = it.body()
                    if (resp!!.verify) {
                        totp.isEnabled = false
                        model.sum++
                        checksum()
                    } else {
                        Log.e("TAG", resp.toString())
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
            val resp = it.body()
            if (resp!!.verify) {
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
                val resp = it.body()!!
                val ac = Account(resp.nickname, "com.maserplay.login.type")
                val acm = AccountManager.get(this)
                acm.addAccountExplicitly(ac, null, null)
                acm.setAuthToken(ac,"cookie" , resp.id)
            }
        }
        model.LoginCheck(LoginVerifySendClass(model.cookie))
    }
}