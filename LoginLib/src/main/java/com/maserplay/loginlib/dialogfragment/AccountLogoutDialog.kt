package com.maserplay.loginlib.dialogfragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.maserplay.loginlib.R

class AccountLogoutDialog : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        return builder.setTitle(getString(R.string.login_logout)).setMessage(getString(R.string.login_notact))
            .setPositiveButton("Ok", null)
            .create()
    }
}