package com.maserplay.appai

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.maserplay.AppAi.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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
            val st = "{\"AppName\": \"" + resources.getString(R.string.app_name) +"\", \"AppVersion\":\"" + VERSION.RELEASE + "\", \"Message\": \"" + er + "\"}"
            val postBody: RequestBody = st.toRequestBody("application/json".toMediaType())
            OkHttpClient().newCall(
                Request.Builder().post(postBody).url("https://games.m2023.ru/api/crash").build()
            ).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            throw IOException("Запрос к серверу не был успешен:" +
                                    " ${response.code} ${response.message}")
                        }
                        // пример получения всех заголовков ответа
                        for ((name, value) in response.headers) {
                            println("$name: $value")
                        }
                        // вывод тела ответа
                        println(response.body.string())
                    }
                }
            })

    }
}