package com.maserplay.appai

import android.accounts.Account
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
import com.maserplay.appai.sync.SyncSpinnerChangeInterval
import com.maserplay.loginlib.LoginViewModel
import com.maserplay.loginlib.activity.LoginActivity
import java.util.Timer
import java.util.TimerTask
import com.maserplay.loginlib.GlobalVariables as loglvar


class SettingsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    //private lateinit var datetimemodel: SyncViewModel
    private val PREFNAME = "api"
    private val PREFNAMEVER = "gptver"
    private lateinit var prefEditor: SharedPreferences.Editor
    private var ac: Account? = null
    private val tim: Timer = Timer(false)
    private var spam: Long = 0
    private var spamsync: Long = 0
    private lateinit var gpterdescr: TextView
    lateinit var spamtv: TextView
    private lateinit var why: TextView
    private lateinit var nickname: TextView
    private lateinit var lmodel: LoginViewModel
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var debug: SwitchCompat
    private lateinit var s_au: SwitchCompat
    private lateinit var s_ll: LinearLayout
    private lateinit var syncll: LinearLayout
    private lateinit var loginll: LinearLayout
    private lateinit var registerbtn: Button
    private lateinit var loginbtn: Button
    private lateinit var apiedt: EditText
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .commit()
        }
        getSharedPreferences(
            GlobalVariables.SHAREDPREFERENCES_NAME,
            MODE_PRIVATE
        ).registerOnSharedPreferenceChangeListener(ShListener())
        loginbtn = findViewById(R.id.btn_prim)
        registerbtn = findViewById(R.id.btn_sec)
        why = findViewById(R.id.why)
        loginll = findViewById(R.id.lllogin)
        syncll = findViewById(R.id.sync)
        lmodel = ViewModelProvider(this)[LoginViewModel::class.java]
        val spinnersync: Spinner = findViewById(R.id.sync_int)
        ArrayAdapter.createFromResource(
            this, R.array.sync, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnersync.adapter = adapter
        }
        when (val v = getSharedPreferences(
            GlobalVariables.SHAREDPREFERENCES_NAME,
            MODE_PRIVATE
        ).getInt("sync_int", 86400)) {
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

            -1 -> { }

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
                if (SystemClock.elapsedRealtime() - spamsync < 10000) {
                    Toast.makeText(this, getString(R.string.sync_spam), Toast.LENGTH_LONG).show()
                    refresh.isRefreshing = false
                } else {
                        val b = Bundle().apply {
                            putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
                            putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
                        }
                        ContentResolver.requestSync(ac, GlobalVariables.PROVIDER, b)
                        ContentResolver.addStatusChangeListener(
                            ContentResolver.SYNC_OBSERVER_TYPE_PENDING or ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE
                        ) {
                            /*apiedt.setText(
                                getSharedPreferences(
                                    GlobalVariables.SHAREDPREFERENCES_NAME,
                                    MODE_PRIVATE
                                ).getString(PREFNAME, "")
                            )*/
                            refresh.isRefreshing = false
                        }
                        Log.i(GlobalVariables.LOGTAG_SYNC, "doSync")
                }
                spamsync = SystemClock.elapsedRealtime()
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        apiedt = findViewById(R.id.EdtApi)
        prefEditor =
            getSharedPreferences(GlobalVariables.SHAREDPREFERENCES_NAME, MODE_PRIVATE).edit()
        spamtv = findViewById(R.id.spamtv)
        debug = findViewById(R.id.debug)
        nickname = findViewById(R.id.nickname)
        gpterdescr = findViewById(R.id.gptver_descr)
        s_au = findViewById(R.id.sync_auto)
        s_ll = findViewById(R.id.l)
        apiedt.addTextChangedListener {
            prefEditor.putString(PREFNAME, apiedt.text.toString().trim()).apply()
        }
        spinner = findViewById(R.id.EdtgptApi)
        ArrayAdapter.createFromResource(
            this,
            R.array.GPTs,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this
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
    }

    override fun onResume() {
        super.onResume()
        when (val input =
            getSharedPreferences(GlobalVariables.SHAREDPREFERENCES_NAME, MODE_PRIVATE).getString(
                PREFNAMEVER,
                "gpt-3.5-turbo"
            )) {
            "gpt-3.5-turbo" -> {
                spinner.setSelection(0)
                gpterdescr.text = getString(R.string.gptver_basic)
            }

            "gpt-3.5-turbo-1106" -> {
                spinner.setSelection(1)
                gpterdescr.text = getString(R.string.gptver_1106)
            }

            "gpt-3.5-turbo-16k" -> {
                spinner.setSelection(2)
                gpterdescr.text = getString(R.string.gptver_16k)
            }

            else -> {
                val arr: MutableList<String> = resources.getStringArray(R.array.GPTs).toMutableList()
                arr.add(input.toString())
                val adapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, arr)
                spinner.adapter = adapter
                spinner.setSelection(arr.size - 1)
                gpterdescr.text = ""
            }
        }
        apiedt.setText(
            getSharedPreferences(GlobalVariables.SHAREDPREFERENCES_NAME, MODE_PRIVATE).getString(
                PREFNAME,
                ""
            )
        )
        ac = loglvar.GetAC(supportFragmentManager, this)
        if (ContentResolver.getMasterSyncAutomatically()) {
            findViewById<TextView>(R.id.master).visibility = View.GONE
            s_au.isChecked = ContentResolver.getSyncAutomatically(ac, GlobalVariables.PROVIDER)
        } else {
            s_au.isChecked = false
            findViewById<TextView>(R.id.master).visibility = View.VISIBLE
        }
        if (ac != null) {
            registerbtn.visibility = View.GONE
            loginbtn.visibility = View.GONE
            why.visibility = View.GONE
            nickname.text = ac!!.name
            loginll.visibility = View.VISIBLE
            syncll.visibility = View.VISIBLE
            OnSync_llChanged()
        } else {
            registerbtn.visibility = View.VISIBLE
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
        ServiceDop.saveText()

    }

    fun LoadBtn(v: View) {
        if (SystemClock.elapsedRealtime() - spam < 3000) {
            tim.cancel()
            Timerr()
            spamtv.visibility = View.VISIBLE
        }
        spam = SystemClock.elapsedRealtime()
        ServiceDop.openText()
    }

    fun Loginbtn(v: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }
    fun Registerbtn(v: View) {
        startActivity(Intent(this, LoginActivity::class.java).putExtra("Register", true))
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
            val acn = loglvar.GetAC(supportFragmentManager, this)!!
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
                prefEditor.putString(PREFNAMEVER, "gpt-3.5-turbo-1106")
                gpterdescr.text = getString(R.string.gptver_1106)
            }

            2 -> {
                prefEditor.putString(PREFNAMEVER, "gpt-3.5-turbo-0613")
                gpterdescr.text = getString(R.string.gptver_16k)
            }

            else -> {
                prefEditor.putString(PREFNAMEVER, spinner.adapter.getItem(position).toString())
                gpterdescr.text = ""
            }
        }
        prefEditor.apply()
    }

    fun logout(v: View) {
        loglvar.logout(ac!!, this)
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