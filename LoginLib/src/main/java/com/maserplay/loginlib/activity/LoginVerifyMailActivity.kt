package com.maserplay.loginlib.activity

import android.accounts.AccountManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.maserplay.loginlib.R

class LoginVerifyMailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_verify_mail)
        if (AccountManager.get(this).accounts.isNotEmpty()) {
            Toast.makeText(this, getString(R.string.error_toomany), Toast.LENGTH_LONG).show()
            finish()}
    }
}