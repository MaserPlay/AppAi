package com.maserplay.appai

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModelProvider

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i("Data", "load")
        ServiceDop(filesDir).openText()
    }
}