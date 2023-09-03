package com.maserplay.appai.dialogfragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.maserplay.AppAi.R
import com.maserplay.appai.ErrorSendClass
import com.maserplay.appai.GlobalVariables
import com.maserplay.appai.Web
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread


class ErrorDialog(private val con: Context, private val er: String) : DialogFragment() {
    private lateinit var tv: TextView
    override fun onAttach(context: Context) {
        super.onAttach(context)
        tv = TextView(context)
        tv.setTypeface(null, Typeface.ITALIC)
        tv.setPadding(60, 0, 60, 0)
        tv.textSize = 18F
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        tv.text = er
        return if (con.getSharedPreferences("Main", AppCompatActivity.MODE_PRIVATE).getBoolean("debug", false)) {
            builder.setTitle(getString(R.string.error_tittle))
                .setMessage(getString(R.string.error_desc))
                .setView(tv)
                .setPositiveButton(getString(R.string.error_pos)) { _, _ -> ok() }
                .setNegativeButton(getString(R.string.error_nega), null)
                .create()
        } else {
            builder.setTitle(getString(R.string.error_tittle))
                .setMessage(getString(R.string.error_desc))
                .setPositiveButton(getString(R.string.error_pos)) { _, _ -> ok() }
                .setNegativeButton(getString(R.string.error_nega), null)
                .create()
        }
    }

    private fun ok(){

        Toast.makeText(context, getString(R.string.error_thanks), Toast.LENGTH_LONG).show()
        thread {
            Retrofit.Builder()
                .baseUrl(GlobalVariables.WEB_ADR_FULL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Web::class.java).errorreport(ErrorSendClass(er))
                .execute()
        }
    }
}