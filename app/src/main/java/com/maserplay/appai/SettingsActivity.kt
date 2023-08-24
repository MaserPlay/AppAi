package com.maserplay.appai

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentResolver
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
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.maserplay.AppAi.R
import com.maserplay.appai.dialogfragment.ErrorDialog
import com.maserplay.appai.login.activity.LoginActivity
import com.maserplay.appai.sync.SyncSpinnerChangeInterval
import com.maserplay.appai.sync.SyncViewModel
import java.util.Timer
import java.util.TimerTask


class SettingsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var datetimemodel: SyncViewModel
    private val PREFSFILE = GlobalVariables.SHAREDPREFERENCES_NAME
    private val PREFNAME = "api"
    private val PREFNAMEVER = "gptver"
    private lateinit var prefEditor: SharedPreferences.Editor
    private var ac: Account? = null
    lateinit var spamtv: TextView
    private val tim: Timer = Timer(false)
    private var spam: Long = 0
    lateinit var gpterdescr: TextView
    lateinit var refresh: SwipeRefreshLayout
    lateinit var debug: SwitchCompat
    lateinit var s_au: SwitchCompat
    lateinit var s_ll: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ac = GlobalVariables.GetAC(supportFragmentManager, this)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .commit()
        }
        val spinnersync: Spinner = findViewById(R.id.sync_int)
        ArrayAdapter.createFromResource(
            this, R.array.sync, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnersync.adapter = adapter
        }
        when (val v = getSharedPreferences(PREFSFILE, MODE_PRIVATE).getInt("sync_int", 86400)) {
            60 -> {
                spinnersync.setSelection(0)
            }
            10800 -> {
                spinnersync.setSelection(1)
            }
            86400 -> {
                spinnersync.setSelection(2)
            }
            604800 -> {
                spinnersync.setSelection(3)
            }
            else -> {
                ErrorDialog(
                    this,
                    "${GlobalVariables.APP_NAME} error in set Sync Interval Settings. Input $v != possible options"
                ).show(
                    supportFragmentManager,
                    GlobalVariables.DIALOGFRAGMENT_TAG
                )
            }
        }
        spinnersync.onItemSelectedListener= SyncSpinnerChangeInterval()
        refresh = findViewById(R.id.refresh)
        refresh.setOnRefreshListener {
            val b = Bundle()
            b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
            ContentResolver.requestSync(ac, GlobalVariables.PROVIDER, b)
            Log.i(GlobalVariables.LOGTAG_SYNC, "doSync")
            refresh.isRefreshing = false
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val apiedt: EditText = findViewById(R.id.EdtApi)
        prefEditor = getSharedPreferences(PREFSFILE, MODE_PRIVATE).edit()
        datetimemodel = ViewModelProvider(this)[SyncViewModel::class.java]
        spamtv = findViewById(R.id.spamtv)
        debug = findViewById(R.id.debug)
        gpterdescr = findViewById(R.id.gptver_descr)
        s_au = findViewById(R.id.sync_auto)
        s_ll = findViewById(R.id.l)
        apiedt.setText(
            applicationContext.getSharedPreferences(PREFSFILE, MODE_PRIVATE).getString(PREFNAME, "")
        )
        if (ac != null) {
            findViewById<Button>(R.id.login).visibility = View.GONE
            findViewById<TextView>(R.id.why).visibility = View.GONE
            findViewById<TextView>(R.id.nickname).text = ac!!.name
            findViewById<LinearLayout>(R.id.lllogin).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.sync).visibility = View.VISIBLE
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
                getSharedPreferences(GlobalVariables.SHAREDPREFERENCES_NAME, MODE_PRIVATE).edit()
                    .putString("lastupdate", it.body()).apply()
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
                gpterdescr.text = getString(R.string.gptver_basic)
            }

            "gpt-3.5-turbo-0301" -> {
                spinner.setSelection(1)
                gpterdescr.text = getString(R.string.gptver_basic)
            }

            "gpt-3.5-turbo-0613" -> {
                spinner.setSelection(2)
                gpterdescr.text = getString(R.string.gptver_0613)
            }

            "gpt-3.5-turbo-16k" -> {
                spinner.setSelection(3)
                gpterdescr.text = getString(R.string.gptver_16k)
            }

            else -> {
                ErrorDialog(
                    this,
                    "${GlobalVariables.APP_NAME} error in set ItemSelected Settings, choose version. Input $input != gpt-3.5-turbo, gpt-3.5-turbo-0301, gpt-3.5-turbo-0613 or gpt-3.5-turbo-16k."
                ).show(
                    supportFragmentManager,
                    GlobalVariables.DIALOGFRAGMENT_TAG
                )
                gpterdescr.text = ""
            }
        }
        s_au.isChecked = getSharedPreferences(
            GlobalVariables.SHAREDPREFERENCES_NAME,
            MODE_PRIVATE
        ).getBoolean("sync_auto", true)
        debug.isChecked = getSharedPreferences(
            GlobalVariables.SHAREDPREFERENCES_NAME,
            MODE_PRIVATE
        ).getBoolean("debug", false)
        debug.setOnCheckedChangeListener { _, isChecked ->
            getSharedPreferences(
                GlobalVariables.SHAREDPREFERENCES_NAME,
                MODE_PRIVATE
            ).edit().putBoolean("debug", isChecked).apply()
        }
        s_au.setOnCheckedChangeListener { _, isChecked ->
            getSharedPreferences(GlobalVariables.SHAREDPREFERENCES_NAME, MODE_PRIVATE).edit()
                .putBoolean("sync_auto", isChecked).apply()
            OnSync_llChanged()
        }
        OnSync_llChanged()
    }
    private fun OnSync_llChanged(){
        if (s_au.isChecked) {
            s_ll.visibility = View.VISIBLE
        } else {
            s_ll.visibility = View.GONE
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
                gpterdescr.text = getString(R.string.gptver_basic)
            }

            1 -> {
                prefEditor.putString(PREFNAMEVER, "gpt-3.5-turbo-0301")
                gpterdescr.text = getString(R.string.gptver_basic)
            }

            2 -> {
                prefEditor.putString(PREFNAMEVER, "gpt-3.5-turbo-0613")
                gpterdescr.text = getString(R.string.gptver_0613)
            }

            3 -> {
                prefEditor.putString(PREFNAMEVER, "gpt-3.5-turbo-16k")
                gpterdescr.text = getString(R.string.gptver_16k)
            }

            else -> {
                ErrorDialog(this,"${GlobalVariables.APP_NAME} error in OnItemSelected Settings, choose version. Position $position > 3").show(
                    supportFragmentManager,
                    GlobalVariables.DIALOGFRAGMENT_TAG
                )
                gpterdescr.text = ""
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
            getSharedPreferences(GlobalVariables.SHAREDPREFERENCES_NAME, MODE_PRIVATE).edit().putString("lastupdate", it.body()).apply()
        }
        datetimemodel.getdatetime()
        prefEditor.apply()
    }
fun logout (v:View){AccountManager.get(this).removeAccountExplicitly(ac)
    ac = null
    findViewById<Button>(R.id.login).visibility = View.VISIBLE
    findViewById<TextView>(R.id.why).visibility = View.VISIBLE
    findViewById<LinearLayout>(R.id.lllogin).visibility = View.GONE}
    override fun onNothingSelected(parent: AdapterView<*>?) {
        return
    }

}