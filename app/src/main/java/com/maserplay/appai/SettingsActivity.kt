package com.maserplay.appai

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.maserplay.AppAi.R
import com.yandex.mobile.ads.banner.AdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import java.lang.String
import java.util.Timer
import java.util.TimerTask


class SettingsActivity : AppCompatActivity() {
    private val PREFSFILE = "Main"
    private val PREFNAME = "api"
    lateinit var spamtv: TextView
    val tim: Timer = Timer(false)
    var spam: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val apiedt: EditText = findViewById(R.id.EdtApi)
        val prefEditor: SharedPreferences.Editor = getSharedPreferences(PREFSFILE, MODE_PRIVATE).edit()
        val res = resources
        val i = 100
        findViewById<TextView>(R.id.textlatest).text = String.format(res.getString(R.string.tokens_left), i.toString())
        spamtv = findViewById(R.id.spamtv)
        apiedt.setText(applicationContext.getSharedPreferences(PREFSFILE, MODE_PRIVATE).getString(PREFNAME, ""))
        apiedt.addTextChangedListener {
            prefEditor.putString(PREFNAME, apiedt.text.toString())
            prefEditor.apply()
        }
        findViewById<Button>(R.id.inBrowserappKey).setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://platform.openai.com/account/api-keys"))) }
        findViewById<Button>(R.id.inBrowserapp).setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://openai.com/"))) }
        findViewById<Button>(R.id.save).setOnClickListener {
            if (SystemClock.elapsedRealtime() - spam < 3000) {tim.cancel()
                Timerr()
                spamtv.visibility = View.VISIBLE }
            spam = SystemClock.elapsedRealtime()
            ServiceDop().saveText() }
        findViewById<Button>(R.id.load).setOnClickListener {
            if (SystemClock.elapsedRealtime() - spam  < 3000) {tim.cancel()
            Timerr()
            spamtv.visibility = View.VISIBLE }
            spam = SystemClock.elapsedRealtime()
            ServiceDop().openText() }
        val mad: BannerAdView = findViewById(R.id.bannerad)
        //mad.setAdUnitId("demo-banner-yandex")
        mad.setAdSize(AdSize.stickySize(100000))
        mad.loadAd(AdRequest.Builder().build())
    }
    fun Timerr(){
        Timer(false).schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread { spamtv.visibility = View.GONE }
            }
        }, 3000)

    }

}