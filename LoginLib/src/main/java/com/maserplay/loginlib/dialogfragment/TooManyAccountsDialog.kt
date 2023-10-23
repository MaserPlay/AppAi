package com.maserplay.loginlib.dialogfragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.maserplay.loginlib.exception.TooManyAccountsException
import com.maserplay.loginlib.R

class TooManyAccountsDialog : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        return builder.setTitle(getString(R.string.error_tittle)).setMessage(getString(R.string.error_toomanyaccounts_desrr))
            .setPositiveButton(getString(R.string.error_settings)) { _, _ -> startActivity(Intent(android.provider.Settings.ACTION_SYNC_SETTINGS))}
            .create()
    }

    override fun onDestroy() {
        super.onDestroy()
        throw TooManyAccountsException()
    }
}