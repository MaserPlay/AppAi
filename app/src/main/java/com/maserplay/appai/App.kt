package com.maserplay.appai

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class App : Application(), Application.ActivityLifecycleCallbacks {
    companion object {
        var isTargetActivityStarted: Boolean = false
    }
    override fun onCreate() {
        super.onCreate()
        Log.i("Data", "load")
        ServiceDop(filesDir).openText()
        this.registerActivityLifecycleCallbacks(this)
        getSharedPreferences(GlobalVariables.SHAREDPREFERENCES_NAME, AppCompatActivity.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(ShListener())
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) { }

    override fun onActivityStarted(activity: Activity) { }

    override fun onActivityResumed(activity: Activity) {
        if (activity is MainActivity) {
            isTargetActivityStarted = true
        }
    }

    override fun onActivityPaused(activity: Activity) {
        if (activity is MainActivity) {
            isTargetActivityStarted = false
        }
    }

    override fun onActivityStopped(activity: Activity) { }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { }

    override fun onActivityDestroyed(activity: Activity) { }

}