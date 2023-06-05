package com.maserplay.appai

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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


class MainActivity : AppCompatActivity() {
    lateinit var model: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val snack = Snackbar.make(
            findViewById(android.R.id.content), getString(R.string.from_github), Snackbar.LENGTH_SHORT).setAction(getString(R.string.to_github)
        ) { startActivity( Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Maserplay/AppAi") ) ) }
        val view = snack.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
        snack.show()
        val btn: Button = findViewById(R.id.button_enter)
        val edt: EditText = findViewById(R.id.EdTxt)
        model = ViewModelProvider(this)[HomeViewModel::class.java]
        val l: ListView = findViewById(R.id.list)
        val wait: TextView = findViewById(R.id.wait)
        val edtt: LinearLayout = findViewById(R.id.ll)
        model.start(this)
        model.ada.observe(this){
            l.adapter = it as ListAdapter?
        }
        model.writen.observe(this){
            edtt.visibility = View.VISIBLE
            wait.visibility = View.GONE
        }
        edt.addTextChangedListener{
            btn.isEnabled = edt.text.toString() != ""
        }
        btn.setOnClickListener {
            this.currentFocus?.let { view ->
                val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
            edtt.visibility = View.GONE
            wait.visibility = View.VISIBLE
            model.exec(edt.text.toString(), applicationContext)
            edt.setText("")
        }
        l.setOnItemClickListener { _, _, position, _ ->
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText(label.toString(), model.getpr()[position].name + " <- " + getString(R.string.copy_watermark)))
            Toast.makeText(applicationContext, getString(R.string.text_copy), Toast.LENGTH_LONG).show() }

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
                Log.i("Data", "save")
                ServiceDop().saveText()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}