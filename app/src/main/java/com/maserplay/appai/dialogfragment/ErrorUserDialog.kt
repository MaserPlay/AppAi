package com.maserplay.appai.dialogfragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.maserplay.AppAi.R
import com.maserplay.appai.ErrorSendClass
import com.maserplay.appai.GlobalVariables
import com.maserplay.appai.Web
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread


class ErrorUserDialog() : DialogFragment() {
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
        thread {
            Retrofit.Builder()
                .baseUrl(GlobalVariables.WEB_ADR_FULL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Web::class.java).errorreport(ErrorSendClass(input.text.toString()))
                .execute()
        }
    }
}