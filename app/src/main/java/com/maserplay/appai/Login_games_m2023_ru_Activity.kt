package com.maserplay.appai

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.maserplay.AppAi.R

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
        model.onCreate(email, password, totp, emailaccept, loginbutton)
        model.showerrorrequest.observe(this){

            Toast.makeText(
                applicationContext,
                getString(R.string.request_error),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}