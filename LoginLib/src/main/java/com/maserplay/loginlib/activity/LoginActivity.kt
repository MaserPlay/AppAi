package com.maserplay.loginlib.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentResolver
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.maserplay.loginlib.LoginClass
import com.maserplay.loginlib.LoginViewModel
import com.maserplay.loginlib.R
import com.maserplay.loginlib.RegisterClass
import com.maserplay.loginlib.dialogfragment.AccountMailDialog


class LoginActivity : AppCompatActivity() {
    private lateinit var model: LoginViewModel
    private lateinit var email: EditText
    private lateinit var password: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.Email)
        if (intent.getBooleanExtra("Register", false)){
            reg(email)
        }
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
                    acm.setAuthToken(ac,"cookie" , it.headers()["Set-Cookie"]!!.split(";")[0])
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
    fun reg(v: View) {
        findViewById<TextView>(R.id.text).text = getString(R.string.login_register)
        findViewById<Button>(R.id.btn_prim).text = getString(R.string.login_register)
        findViewById<Button>(R.id.btn_prim).setOnClickListener { regbutton(findViewById<Button>(R.id.btn_prim)) }
        findViewById<Button>(R.id.btn_sec).text = getString(R.string.login_login)
        findViewById<Button>(R.id.btn_sec).setOnClickListener { login(findViewById<Button>(R.id.btn_sec)) }
    }

    fun login(v: View) {
        findViewById<TextView>(R.id.text).text = getString(R.string.login_login)
        findViewById<Button>(R.id.btn_prim).text = getString(R.string.login_login)
        findViewById<Button>(R.id.btn_prim).setOnClickListener { loginButton(findViewById<Button>(R.id.btn_prim)) }
        findViewById<Button>(R.id.btn_sec).text  = getString(R.string.login_register)
        findViewById<Button>(R.id.btn_sec).setOnClickListener { reg(findViewById<Button>(R.id.btn_sec)) }
    }

}