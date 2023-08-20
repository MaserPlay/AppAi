package com.maserplay.appai.login

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class AuthService : Service() {
    private lateinit var mAuthenticator: AuthAccount

    override fun onCreate() {
        Log.i("LoginTAG", "StartService")
        super.onCreate()
        mAuthenticator = AuthAccount(applicationContext)
    }
    override fun onBind(intent: Intent?): IBinder {
        Log.i("LoginTAG", "BindToService")
        return mAuthenticator.iBinder
    }
}