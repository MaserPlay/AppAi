package com.maserplay.loginlib.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentResolver
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.maserplay.loginlib.GlobalVariables
import com.maserplay.loginlib.LoginViewModel
import com.maserplay.loginlib.R
import com.maserplay.loginlib.send_get_classes.LoginClass


class LoginActivity : AppCompatActivity() {
    private lateinit var model: LoginViewModel
    private lateinit var email: EditText
    private lateinit var password: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        findViewById<TextView>(R.id.textView3).text = "getString(R.string.login_with, )"
        email = findViewById(R.id.Email)
        password = findViewById(R.id.Password)
        model = ViewModelProvider(this)[LoginViewModel::class.java]
        if (AccountManager.get(this).accounts.isNotEmpty()) {
            Toast.makeText(this, getString(R.string.error_toomany), Toast.LENGTH_LONG).show()
        finish()}
    }
    fun loginButton(v: View){
        v.isEnabled = false
        model.LoginResponseLiveData.observe(this) {
            if (!it.isSuccessful) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.request_error),
                    Toast.LENGTH_LONG
                ).show()

                v.isEnabled = true
            } else {
                val resp = it.body()
                runOnUiThread {
                    if (resp?.email == false) {
                        email.error = resources.getString(R.string.login_error_email)
                    }
                    if (resp?.password == false) {
                        password.error =
                            resources.getString(R.string.login_error_password)
                        v.isEnabled = true
                        return@runOnUiThread
                    } else {
                    }
                }
            }
        }
        model.Login(LoginClass(email.text.toString(), password.text.toString()))
    }
    fun reg(v: View){
        startActivity(Intent(this, RegisterActivity::class.java))
    }
    
}