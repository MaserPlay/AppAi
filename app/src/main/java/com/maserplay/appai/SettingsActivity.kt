package com.maserplay.appai

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.maserplay.AppAi.LoginActivity
import com.maserplay.AppAi.R
import java.util.Timer
import java.util.TimerTask


class SettingsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private val PREFSFILE = "Main"
    private val PREFNAME = "api"
    private val PREFNAMEVER = "gptver"
    private lateinit var prefEditor: SharedPreferences.Editor
    lateinit var spamtv: TextView
    private val tim: Timer = Timer(false)
    private var spam: Long = 0
    lateinit var gpterdescr: TextView

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
        prefEditor = getSharedPreferences(PREFSFILE, MODE_PRIVATE).edit()
        spamtv = findViewById(R.id.spamtv)
        gpterdescr = findViewById(R.id.gptver_descr)
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
        findViewById<Button>(R.id.login).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))}
        findViewById<Button>(R.id.load).setOnClickListener {
            if (SystemClock.elapsedRealtime() - spam  < 3000) {tim.cancel()
            Timerr()
            spamtv.visibility = View.VISIBLE }
            spam = SystemClock.elapsedRealtime()
            ServiceDop().openText() }
        val spinner: Spinner = findViewById(R.id.EdtgptApi)
        ArrayAdapter.createFromResource(
            this,
            R.array.GPTs,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this
        when (getSharedPreferences(PREFSFILE, MODE_PRIVATE).getString(PREFNAMEVER, "gpt-3.5-turbo")) {
            "gpt-3.5-turbo" -> { spinner.setSelection(0)
                getdescr(0) }
            "gpt-3.5-turbo-0301" -> {spinner.setSelection(1)
                getdescr(1)}
            "gpt-3.5-turbo-0613" -> {spinner.setSelection(2)
                getdescr(2)}
            "gpt-3.5-turbo-16k" -> {spinner.setSelection(3)
                getdescr(3)}
        }
    }
    private fun Timerr(){
        Timer(false).schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread { spamtv.visibility = View.GONE }
            }
        }, 3000)

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                prefEditor.putString(PREFNAMEVER, "gpt-3.5-turbo")
                getdescr(0)
            }
            1 -> {
                prefEditor.putString(PREFNAMEVER, "gpt-3.5-turbo-0301")
                getdescr(1)
            }
            2 -> {
                prefEditor.putString(PREFNAMEVER, "gpt-3.5-turbo-0613")
                getdescr(2)
            }
            3 -> {
                prefEditor.putString(PREFNAMEVER, "gpt-3.5-turbo-16k")
                getdescr(3)
            } else -> {
            Log.e("TAG", "error$position")}
        }
        prefEditor.apply()
    }
 fun getdescr(pos: Int){
     when (getSharedPreferences(PREFSFILE, MODE_PRIVATE).getString(PREFNAMEVER, "gpt-3.5-turbo")) {
         "gpt-3.5-turbo" -> { gpterdescr.text = getString(R.string.gptver_basic) }
         "gpt-3.5-turbo-0301" -> {gpterdescr.text = getString(R.string.gptver_basic)}
         "gpt-3.5-turbo-0613" -> {gpterdescr.text = getString(R.string.gptver_0613)}
         "gpt-3.5-turbo-16k" -> {gpterdescr.text = getString(R.string.gptver_16k)}
     }
 }
    override fun onNothingSelected(parent: AdapterView<*>?) {
        return
    }

}