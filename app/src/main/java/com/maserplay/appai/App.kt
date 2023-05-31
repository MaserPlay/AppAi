package com.maserplay.appai

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.yandex.mobile.ads.common.MobileAds

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i("Data", "load")
        ServiceDop(filesDir).openText()
        MobileAds.initialize(this) { Log.d("YandexAd", "SDK initialized") }
    }
}