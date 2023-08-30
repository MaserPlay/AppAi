package com.maserplay.appai

import android.R.attr.label
import android.accounts.AccountManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Build
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
import com.maserplay.appai.dialogfragment.ErrorDialog
import com.maserplay.appai.dialogfragment.ErrorUserDialog
import com.maserplay.appai.login.activity.LoginActivity
import com.maserplay.appai.sync.SyncViewModel
import java.util.Timer
import java.util.TimerTask


class MainActivity : AppCompatActivity() {
    val CHANNEL_ID = "1"
    private lateinit var model: HomeViewModel
    private lateinit var datetimemodel: SyncViewModel
    private lateinit var llwait: LinearLayout
    private lateinit var wait: TextView
    private lateinit var edtt: LinearLayout
    private lateinit var edt: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        CreateLoginSnackbar()
        CreateGithubSnackbar()
        val btn: Button = findViewById(R.id.button_enter)
        edt = findViewById(R.id.EdTxt)
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
            ErrorDialog(this,"${GlobalVariables.APP_NAME} $it").show(supportFragmentManager, GlobalVariables.DIALOGFRAGMENT_TAG)
        }
        edt.addTextChangedListener {
            btn.isEnabled = edt.text.toString() != ""
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
    fun Button_Enter(v: View){
        this.currentFocus?.let { view ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
        edtt.visibility = View.GONE
        llwait.visibility = View.VISIBLE
        model.exec(edt.text.toString(), applicationContext)
        edt.setText("")
        datetimemodel.setdatetime(this)
    }

    override fun onResume() {
        super.onResume()
        val api: String =
            getSharedPreferences(GlobalVariables.SHAREDPREFERENCES_NAME, MODE_PRIVATE).getString("api", "").toString()
        if (api == "") {
            edtt.visibility = View.GONE
            wait.text = getString(R.string.api_key_empty)
            llwait.visibility = View.VISIBLE
        }
    }
    private fun CreateLoginSnackbar() {
        if (AccountManager.get(this).accounts.isEmpty()) {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    val snack = Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.login_why),
                        Snackbar.LENGTH_SHORT
                    ).setAction(
                        getString(R.string.login_login)
                    ) {
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    }
                    val view = snack.view
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.TOP
                    view.layoutParams = params
                    snack.show()
                }
            }, 3000)
        }
    }

    private fun CreateGithubSnackbar() {
        val snack = Snackbar.make(
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
        val view = snack.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
        snack.show()
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
                ErrorUserDialog().show(supportFragmentManager, GlobalVariables.DIALOGFRAGMENT_TAG)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "The answers"
            val descriptionText = "Get notified when AppAi answers your question!"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}