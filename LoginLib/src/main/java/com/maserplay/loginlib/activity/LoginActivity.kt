package com.maserplay.loginlib.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentResolver
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.maserplay.loginlib.LoginViewModel
import com.maserplay.loginlib.R
import com.maserplay.loginlib.LoginClass


class LoginActivity : AppCompatActivity() {
    private lateinit var model: LoginViewModel
    private lateinit var email: EditText
    private lateinit var password: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.Email)
        password = findViewById(R.id.Password)
        model = ViewModelProvider(this)[LoginViewModel::class.java]
        if (AccountManager.get(this).accounts.isNotEmpty()) {
            Toast.makeText(this, getString(R.string.error_toomany), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    fun loginButton(v: View) {
        v.isEnabled = false
        model.LoginResponseLiveData.observe(this) {
            if (!it.isSuccessful) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.request_error),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val resp = it.body()
                if (resp?.success == false) {
                    email.error = resources.getString(R.string.login_error_email)
                    password.error =
                        resources.getString(R.string.login_error_password)
                } else {
                    val ac = Account(resp!!.user.login, "com.maserplay.login.type")
                    val acm = AccountManager.get(this)
                    acm.addAccountExplicitly(ac, null, null)
                    acm.setAuthToken(ac,"cookie" , it.headers()["Set-Cookie"]!!)
                    val b = Bundle()
                    b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
                    ContentResolver.requestSync(ac, "com.maserplay.chtgpt.sync.provider", b)
                    Log.i("GlobalVariables.LOGTAG_SYNC", "doSync")
                    ContentResolver.addPeriodicSync(ac, "com.maserplay.chtgpt.sync.provider", Bundle.EMPTY, 86400)
                    getSharedPreferences("Main", MODE_PRIVATE).edit().putInt("sync_int", 86400).apply()
                    finish()
                }
            }
            v.isEnabled = true
        }
        model.Login(LoginClass(email.text.toString(), password.text.toString()))
    }
    fun reg(v: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

}