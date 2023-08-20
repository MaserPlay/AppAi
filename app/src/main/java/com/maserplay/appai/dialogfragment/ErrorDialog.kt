package com.maserplay.appai.dialogfragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.maserplay.AppAi.R
import com.maserplay.appai.ErrorSendClass
import com.maserplay.appai.web
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread


class ErrorDialog(errormsgr: String) : DialogFragment() {

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
        thread {
            Retrofit.Builder()
                .baseUrl(web.Base)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(web::class.java).errorreport(ErrorSendClass(er))
                .execute()
        }
    }
}