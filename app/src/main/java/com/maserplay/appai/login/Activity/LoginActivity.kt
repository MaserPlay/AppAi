package com.maserplay.appai.login.Activity

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.maserplay.appai.login.LoginViewModel
import com.maserplay.AppAi.R
import com.maserplay.appai.login.send_get_classes.LoginClass


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val email: EditText = findViewById(R.id.Email)
        val password: EditText = findViewById(R.id.Password)
        val loginbutton: Button = findViewById(R.id.login)
        val model = ViewModelProvider(this)[LoginViewModel::class.java]
        loginbutton.setOnClickListener {
            loginbutton.isEnabled = false
            model.LoginResponseLiveData.observe(this) {
                if (!it.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.request_error),
                        Toast.LENGTH_LONG
                    ).show()

                    loginbutton.isEnabled = true
                } else {
                    val resp = it.body()
                    runOnUiThread {
                        if (resp?.email == false) {
                            email.error = resources.getString(R.string.login_error_email)
                        }
                        if (resp?.password == false) {
                            password.error =
                                resources.getString(R.string.login_error_password)
                            loginbutton.isEnabled = true
                            return@runOnUiThread
                        } else {
                            if (resp?.hasaccept as Boolean) {
                                model.cookie = resp.acceptcookie.toString()
                                loginbutton.isEnabled = true
                                if (resp.accept?.totp as Boolean) {
                                    model.totp = View.VISIBLE
                                    model.sum--
                                }
                                if (resp.accept?.emailaccept as Boolean) {
                                    model.emaillogin = View.VISIBLE
                                    model.sum--
                                }
                                startActivity(Intent(this, LoginVerifyActivity::class.java))
                            } else {
                                if (resp.verificate == true) {
                                    val ac = Account(resp.nickname, "com.maserplay.login.type")
                                    val acm = AccountManager.get(this)
                                    acm.addAccountExplicitly(ac, null, null)
                                    acm.setAuthToken(ac,"cookie" , resp.id)
                                } else {
                                    startActivity(Intent(this, LoginVerifyMailActivity::class.java))
                                }
                            }
                        }
                    }
                }
            }
            model.Login(LoginClass(email.text.toString(), password.text.toString()))
        }
    }
}