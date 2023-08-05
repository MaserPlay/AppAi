package com.maserplay.appai

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.EditText
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


class Error_report_user_dialog() : DialogFragment() {
    private lateinit var input: EditText
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        return builder.setTitle(getString(R.string.error_tittle)).setMessage(getString(R.string.error_descrs))
            .setView(input)
            .setPositiveButton(getString(R.string.error_pos)) { _, _ -> ok() }
            .setNegativeButton(getString(R.string.error_nega), null)
            .create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        input = EditText(context)
    }

    private fun ok(){

        Toast.makeText(context, getString(R.string.error_thanks), Toast.LENGTH_LONG).show()
        val st = "{\"AppName\": \"" + resources.getString(R.string.app_name) +"\", \"AppVersion\":\"" + Build.VERSION.RELEASE + "\", \"Message\": \"" + input.text + "\"}"
        val postBody: RequestBody = st.toRequestBody("application/json".toMediaType())
        OkHttpClient().newCall(
            Request.Builder().post(postBody).url("https://games.m2023.ru/api/crash").build()
        ).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(context, getString(R.string.request_error), Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Toast.makeText(context, getString(R.string.request_error), Toast.LENGTH_LONG).show()
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