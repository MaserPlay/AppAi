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
    private val tim: Timer = Timer(false)
    private var spam: Long = 0
    lateinit var gpterdescr: TextView
    lateinit var spamtv: TextView
    lateinit var why: TextView
    lateinit var nickname: TextView
    lateinit var refresh: SwipeRefreshLayout
    lateinit var debug: SwitchCompat
    lateinit var s_au: SwitchCompat
    lateinit var s_ll: LinearLayout
    lateinit var syncll: LinearLayout
    lateinit var loginll: LinearLayout
    lateinit var loginbtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .commit()
        }
        loginbtn = findViewById(R.id.login)
        why = findViewById(R.id.why)
        loginll = findViewById(R.id.lllogin)
        syncll = findViewById(R.id.sync)
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
        spinnersync.onItemSelectedListener = SyncSpinnerChangeInterval()
        refresh = findViewById(R.id.refresh)
        refresh.setOnRefreshListener {
            if (ac == null) {
                refresh.isRefreshing = false
            } else {
                val b = Bundle().apply {
                    putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
                    putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
                }
                ContentResolver.requestSync(ac, GlobalVariables.PROVIDER, b)
                ContentResolver.addStatusChangeListener(
                    ContentResolver.SYNC_OBSERVER_TYPE_PENDING or ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE
                ) { refresh.isRefreshing = false }
                Log.i(GlobalVariables.LOGTAG_SYNC, "doSync")
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val apiedt: EditText = findViewById(R.id.EdtApi)
        prefEditor = getSharedPreferences(PREFSFILE, MODE_PRIVATE).edit()
        datetimemodel = ViewModelProvider(this)[SyncViewModel::class.java]
        spamtv = findViewById(R.id.spamtv)
        debug = findViewById(R.id.debug)
        nickname = findViewById(R.id.nickname)
        gpterdescr = findViewById(R.id.gptver_descr)
        s_au = findViewById(R.id.sync_auto)
        s_ll = findViewById(R.id.l)
        apiedt.setText(
            applicationContext.getSharedPreferences(PREFSFILE, MODE_PRIVATE).getString(PREFNAME, "")
        )
        apiedt.addTextChangedListener {
            prefEditor.putString(PREFNAME, apiedt.text.toString()).apply()
            datetimemodel.setdatetime(this)
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
        s_au.setOnCheckedChangeListener { _, _ -> OnSync_llChanged() }
        OnSync_llChanged()
    }

    override fun onResume() {
        super.onResume()
        ac = GlobalVariables.GetAC(supportFragmentManager, this)
        if (ContentResolver.getMasterSyncAutomatically()) {
            findViewById<TextView>(R.id.master).visibility = View.GONE
            s_au.isChecked = ContentResolver.getSyncAutomatically(ac, GlobalVariables.PROVIDER)
        } else {
            s_au.isEnabled = false
            s_au.isChecked = false
            findViewById<TextView>(R.id.master).visibility = View.VISIBLE
        }
        if (ac != null) {
            loginbtn.visibility = View.GONE
            why.visibility = View.GONE
            nickname.text = ac!!.name
            loginll.visibility = View.VISIBLE
            syncll.visibility = View.VISIBLE
        } else {
            loginbtn.visibility = View.VISIBLE
            why.visibility = View.VISIBLE
            loginll.visibility = View.GONE
            syncll.visibility = View.GONE
        }
    }

    fun SaveBtn(v: View) {
        if (SystemClock.elapsedRealtime() - spam < 3000) {
            tim.cancel()
            Timerr()
            spamtv.visibility = View.VISIBLE
        }
        spam = SystemClock.elapsedRealtime()
        ServiceDop().saveText()

    }

    fun LoadBtn(v: View) {
        if (SystemClock.elapsedRealtime() - spam < 3000) {
            tim.cancel()
            Timerr()
            spamtv.visibility = View.VISIBLE
        }
        spam = SystemClock.elapsedRealtime()
        ServiceDop().openText()
    }

    fun Loginbtn(v: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun OpenOpenAI(v: View) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://openai.com/")
            )
        )
    }

    fun OpenOpenAIKeys(v: View) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://platform.openai.com/account/api-keys")
            )
        )

    }

    private fun OnSync_llChanged() {
        if (s_au.isChecked) {
            s_ll.visibility = View.VISIBLE
        } else {
            val acn = GlobalVariables.GetAC(supportFragmentManager, this)!!
            ContentResolver.cancelSync(
                acn,
                GlobalVariables.PROVIDER
            )
            ContentResolver.removePeriodicSync(
                acn,
                GlobalVariables.PROVIDER,
                Bundle.EMPTY
            )
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
                ErrorDialog(
                    this,
                    "${GlobalVariables.APP_NAME} error in OnItemSelected Settings, choose version. Position $position > 3"
                ).show(
                    supportFragmentManager,
                    GlobalVariables.DIALOGFRAGMENT_TAG
                )
                gpterdescr.text = ""
            }
        }
        datetimemodel.setdatetime(this)
        prefEditor.apply()
    }

    fun logout(v: View) {
        AccountManager.get(this).removeAccountExplicitly(ac)
        ac = null
        loginbtn.visibility = View.VISIBLE
        why.visibility = View.VISIBLE
        loginll.visibility = View.GONE
        syncll.visibility = View.GONE
        prefEditor.putInt("sync_int", -1).apply()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        return
    }

}