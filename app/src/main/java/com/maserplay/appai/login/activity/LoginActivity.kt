package com.maserplay.appai.login.activity

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
import com.maserplay.appai.login.LoginViewModel
import com.maserplay.AppAi.R
import com.maserplay.appai.GlobalVariables
import com.maserplay.appai.SettingsActivity
import com.maserplay.appai.login.send_get_classes.LoginClass


class LoginActivity : AppCompatActivity() {
    private lateinit var model: LoginViewModel
    private lateinit var email: EditText
    private lateinit var password: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        findViewById<TextView>(R.id.textView3).text = getString(R.string.login_with, GlobalVariables.WEB_NAME)
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
                        if (resp?.hasaccept as Boolean) {
                            model.cookie = resp.acceptcookie.toString()
                            v.isEnabled = true
                            if (resp.accept?.totp as Boolean) {
                                model.totp = View.VISIBLE
                                model.sum--
                            }
                            if (resp.accept?.emailaccept as Boolean) {
                                model.emaillogin = View.VISIBLE
                                model.sum--
                            }
                            val int = Intent(this, LoginVerifyActivity::class.java)
                            int.putExtra("email", model.emaillogin)
                            int.putExtra("totp", model.totp)
                            int.putExtra("cookie", model.cookie)
                            startActivity(int)
                        } else {
                            if (resp.verificate == true) {
                                val ac = Account(resp.nickname, "com.maserplay.login.type")
                                val acm = AccountManager.get(this)
                                acm.addAccountExplicitly(ac, null, null)
                                acm.setAuthToken(ac,"cookie" , resp.id)
                                val b = Bundle()
                                b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
                                ContentResolver.requestSync(ac, GlobalVariables.PROVIDER, b)
                                Log.i(GlobalVariables.LOGTAG_SYNC, "doSync")
                                ContentResolver.addPeriodicSync(ac, GlobalVariables.PROVIDER, Bundle.EMPTY, 86400)
                                getSharedPreferences(GlobalVariables.SHAREDPREFERENCES_NAME, MODE_PRIVATE).edit().putInt("sync_int", 86400).apply()
                                startActivity(Intent(this, SettingsActivity::class.java))
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