package com.maserplay.loginlib.dialogfragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.maserplay.loginlib.R

class AccountMailDialog : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        return builder.setTitle(getString(R.string.login_verifymail_tittle)).setMessage(getString(R.string.login_verifymail_descr))
            .setPositiveButton("Ok", null)
            .create()
    }
}