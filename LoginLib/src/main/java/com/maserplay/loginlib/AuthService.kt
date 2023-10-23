package com.maserplay.loginlib

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AuthService : Service() {
    private lateinit var mAuthenticator: AuthAccount

    override fun onCreate() {
        super.onCreate()
        mAuthenticator = AuthAccount(applicationContext)
    }
    override fun onBind(intent: Intent?): IBinder {
        return mAuthenticator.iBinder
    }
}