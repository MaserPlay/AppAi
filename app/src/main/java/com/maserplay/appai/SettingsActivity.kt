package com.maserplay.appai

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.maserplay.AppAi.R
import com.maserplay.appai.login.LoginActivity
import com.maserplay.appai.sync.SyncViewModel
import java.util.Timer
import java.util.TimerTask


class SettingsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var datetimemodel: SyncViewModel
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
        datetimemodel = ViewModelProvider(this)[SyncViewModel::class.java]
        spamtv = findViewById(R.id.spamtv)
        gpterdescr = findViewById(R.id.gptver_descr)
        apiedt.setText(
            applicationContext.getSharedPreferences(PREFSFILE, MODE_PRIVATE).getString(PREFNAME, "")
        )
        if (applicationContext.getSharedPreferences(PREFSFILE, MODE_PRIVATE)
                .getString("cookie", "") != ""
        ) {
            findViewById<Button>(R.id.login).visibility = View.GONE
            findViewById<TextView>(R.id.why).visibility = View.GONE
            findViewById<TextView>(R.id.nickname).text =
                applicationContext.getSharedPreferences(PREFSFILE, MODE_PRIVATE)
                    .getString("nickname", "example@example.com")
            findViewById<LinearLayout>(R.id.lllogin).visibility = View.VISIBLE
        }
        apiedt.addTextChangedListener {
            prefEditor.putString(PREFNAME, apiedt.text.toString()).apply()
            datetimemodel.datetime.observe(this) {
                if (!it.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.request_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
                getSharedPreferences("Main", MODE_PRIVATE).edit().putString("lastupdate", it.body()).apply()
            }
            datetimemodel.getdatetime()
        }
        findViewById<Button>(R.id.inBrowserappKey).setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://platform.openai.com/account/api-keys")
                )
            )
        }
        findViewById<Button>(R.id.inBrowserapp).setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://openai.com/")
                )
            )
        }
        findViewById<Button>(R.id.save).setOnClickListener {
            if (SystemClock.elapsedRealtime() - spam < 3000) {
                tim.cancel()
                Timerr()
                spamtv.visibility = View.VISIBLE
            }
            spam = SystemClock.elapsedRealtime()
            ServiceDop().saveText()
        }
        findViewById<Button>(R.id.login).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        findViewById<Button>(R.id.load).setOnClickListener {
            if (SystemClock.elapsedRealtime() - spam < 3000) {
                tim.cancel()
                Timerr()
                spamtv.visibility = View.VISIBLE
            }
            spam = SystemClock.elapsedRealtime()
            ServiceDop().openText()
        }
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
        when (val input =
            getSharedPreferences(PREFSFILE, MODE_PRIVATE).getString(PREFNAMEVER, "gpt-3.5-turbo")) {
            "gpt-3.5-turbo" -> {
                spinner.setSelection(0)
                getdescr("gpt-3.5-turbo")
            }

            "gpt-3.5-turbo-0301" -> {
                spinner.setSelection(1)
                getdescr("gpt-3.5-turbo-0301")
            }

            "gpt-3.5-turbo-0613" -> {
                spinner.setSelection(2)
                getdescr("gpt-3.5-turbo-0613")
            }

            "gpt-3.5-turbo-16k" -> {
                spinner.setSelection(3)
                getdescr("gpt-3.5-turbo-16k")
            }

            else -> {
                Error_report_dialog("AppAi error in set ItemSelected Settings, choose version. Input $input != gpt-3.5-turbo, gpt-3.5-turbo-0301, gpt-3.5-turbo-0613 or gpt-3.5-turbo-16k.").show(
                    supportFragmentManager,
                    "error"
                )
            }
        }
    }

    private fun Timerr() {
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
                getdescr("gpt-3.5-turbo")
            }

            1 -> {
                prefEditor.putString(PREFNAMEVER, "gpt-3.5-turbo-0301")
                getdescr("gpt-3.5-turbo-0301")
            }

            2 -> {
                prefEditor.putString(PREFNAMEVER, "gpt-3.5-turbo-0613")
                getdescr("gpt-3.5-turbo-0613")
            }

            3 -> {
                prefEditor.putString(PREFNAMEVER, "gpt-3.5-turbo-16k")
                getdescr("gpt-3.5-turbo-16k")
            }

            else -> {
                Error_report_dialog("AppAi error in OnItemSelected Settings, choose version. Position $position > 3").show(
                    supportFragmentManager,
                    "error"
                )
            }
        }
        datetimemodel.datetime.observe(this) {
            if (!it.isSuccessful) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.request_error),
                    Toast.LENGTH_LONG
                ).show()
            }
            getSharedPreferences("Main", MODE_PRIVATE).edit().putString("lastupdate", it.body()).apply()
        }
        datetimemodel.getdatetime()
        prefEditor.apply()
    }

    fun getdescr(name: String) {
        when (name) {
            "gpt-3.5-turbo" -> {
                gpterdescr.text = getString(R.string.gptver_basic)
            }

            "gpt-3.5-turbo-0301" -> {
                gpterdescr.text = getString(R.string.gptver_basic)
            }

            "gpt-3.5-turbo-0613" -> {
                gpterdescr.text = getString(R.string.gptver_0613)
            }

            "gpt-3.5-turbo-16k" -> {
                gpterdescr.text = getString(R.string.gptver_16k)
            }

            else -> {
                Error_report_dialog("AppAi error in getdesr in OnItemSelected Settings, choose version. Input $name != gpt-3.5-turbo, gpt-3.5-turbo-0301, gpt-3.5-turbo-0613 or gpt-3.5-turbo-16k.").show(
                    supportFragmentManager,
                    "error"
                )
            }
        }
    }
fun logout (v:View){getSharedPreferences("Main", MODE_PRIVATE).edit()
    .putString("cookie", null).putString("nickname", null).apply()
    findViewById<Button>(R.id.login).visibility = View.VISIBLE
    findViewById<TextView>(R.id.why).visibility = View.VISIBLE
    findViewById<LinearLayout>(R.id.lllogin).visibility = View.GONE}
    override fun onNothingSelected(parent: AdapterView<*>?) {
        return
    }

}