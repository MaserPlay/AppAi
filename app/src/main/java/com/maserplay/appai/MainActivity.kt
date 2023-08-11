package com.maserplay.appai

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.maserplay.AppAi.R
import com.maserplay.appai.login.LoginActivity
import com.maserplay.appai.sync.SyncViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var model: HomeViewModel
    private lateinit var datetimemodel: SyncViewModel
    private lateinit var llwait: LinearLayout
    private lateinit var wait: TextView
    private lateinit var edtt: LinearLayout
    private val PREF_NAME = "api"
    private val PREFS_FILE = "Main"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var snack = Snackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.from_github),
            Snackbar.LENGTH_SHORT
        ).setAction(
            getString(R.string.to_github)
        ) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/Maserplay/AppAi")
                )
            )
        }
        var view = snack.view
        var params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
        snack.show()
        snack = Snackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.from_github),
            Snackbar.LENGTH_SHORT
        ).setAction(
            getString(R.string.to_github)
        ) {
                    startActivity(Intent(this, LoginActivity::class.java))
        }
        view = snack.view
        params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
        snack.show()
        val btn: Button = findViewById(R.id.button_enter)
        val edt: EditText = findViewById(R.id.EdTxt)
        model = ViewModelProvider(this)[HomeViewModel::class.java]
        datetimemodel = ViewModelProvider(this)[SyncViewModel::class.java]
        val l: ListView = findViewById(R.id.list)
        wait = findViewById(R.id.wait)
        llwait = findViewById(R.id.llwait)
        edtt = findViewById(R.id.ll)
        model.start(this)
        model.ada.observe(this) {
            l.adapter = it as ListAdapter?
        }
        model.writen.observe(this) {
            edtt.visibility = View.VISIBLE
            llwait.visibility = View.GONE
        }
        model.errortr.observe(this) {
            Error_report_dialog("AppAi $it").show(supportFragmentManager, "error")
        }
        edt.addTextChangedListener {
            btn.isEnabled = edt.text.toString() != ""
        }
        btn.setOnClickListener {
            this.currentFocus?.let { view ->
                val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
            edtt.visibility = View.GONE
            llwait.visibility = View.VISIBLE
            model.exec(edt.text.toString(), applicationContext)
            edt.setText("")
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
        l.setOnItemClickListener { _, _, position, _ ->
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(
                ClipData.newPlainText(
                    label.toString(),
                    model.getpr()[position].name + " <- " + getString(R.string.copy_watermark)
                )
            )
            Toast.makeText(applicationContext, getString(R.string.text_copy), Toast.LENGTH_LONG)
                .show()
        }

    }

    override fun onResume() {
        super.onResume()
        val api: String =
            getSharedPreferences(PREFS_FILE, MODE_PRIVATE).getString(PREF_NAME, "alo").toString()
        if ((api == "alo") || (api == "")) {
            edtt.visibility = View.GONE
            wait.text = getString(R.string.api_key_empty)
            llwait.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }

            R.id.clear -> {
                model.clear()
                ServiceDop().clear()
                ServiceDop().saveText()
                return true
            }

            R.id.report -> {
                Error_report_user_dialog().show(supportFragmentManager, "error")
            }
        }
        return super.onOptionsItemSelected(item)
    }

}