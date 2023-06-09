package com.maserplay.appai

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.maserplay.AppAi.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class Error_report_dialog(errormsgr: String) : DialogFragment() {

    private val er = errormsgr
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        return builder.setTitle(getString(R.string.error_tittle)).setMessage(getString(R.string.error_desc))
            .setPositiveButton(getString(R.string.error_pos)) { _, _ -> ok() }
            .setNegativeButton(getString(R.string.error_nega), null)
            .create()
    }

    private fun ok(){
        Toast.makeText(context, getString(R.string.error_thanks), Toast.LENGTH_LONG).show()
        OkHttpClient().newCall(Request.Builder().url("https://games.m2023.ru/api/crash_$er").build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {Log.i("log",("Запрос к серверу не был успешен:" +
                    " Server not found"))
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.i("log","Запрос к серверу не был успешен:" +
                                " ${response.code} ${response.message}")
                    } else{
                        Log.i("log", "good")

                    }
                }
            }
        })
    }
}