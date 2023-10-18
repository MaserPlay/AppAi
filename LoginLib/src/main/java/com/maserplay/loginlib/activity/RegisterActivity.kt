package com.maserplay.loginlib.activity

import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.maserplay.loginlib.LoginViewModel
import com.maserplay.loginlib.R
import com.maserplay.loginlib.dialogfragment.AccountMailDialog
import com.maserplay.loginlib.RegisterClass

class RegisterActivity : AppCompatActivity() {
    private lateinit var model: LoginViewModel
    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        if (AccountManager.get(this).accounts.isNotEmpty()) {
            Toast.makeText(this, getString(R.string.error_toomany), Toast.LENGTH_LONG).show()
            finish()
        }
        model = ViewModelProvider(this)[LoginViewModel::class.java]
        email = findViewById(R.id.Email)
        password = findViewById(R.id.Password)
    }

    fun regbutton(v: View) {
        v.isEnabled = false
        email.error = null
        password.error = null
        model.RegisterResponseLiveData.observe(this) {
            if (!it.isSuccessful) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.request_error),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (!it.body()!!.success) {
                    email.error = resources.getString(R.string.login_error_email)
                    password.error = resources.getString(R.string.login_error_password)
                } else {
                    AccountMailDialog().show(supportFragmentManager, "custom")
                }
            }
            v.isEnabled = true
        }
        model.Register(
            RegisterClass(
                email.text.toString(),
                password.text.toString(),
                email.text.toString()
            )
        )
    }

    fun login(v: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}