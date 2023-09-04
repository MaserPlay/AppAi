package com.maserplay.appai.dialogfragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.maserplay.AppAi.R

class AccountLogoutDialog : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        return builder.setTitle(getString(R.string.error_tittle)).setMessage("a")
            .setPositiveButton("Ok", null)
            .create()
    }
}