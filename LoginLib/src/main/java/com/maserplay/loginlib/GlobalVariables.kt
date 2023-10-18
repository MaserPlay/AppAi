package com.maserplay.loginlib

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import androidx.fragment.app.FragmentManager
import com.maserplay.loginlib.dialogfragment.TooManyAccountsDialog

object GlobalVariables{
    const val WEB_ADR_FULL = "https://games.m2023.ru/"
    const val DIALOGFRAGMENT_TAG = "error"
    const val LOGTAG_LOGIN = "LoginTAG"
    fun GetAC(supportFragmentManager: FragmentManager, con: Context) : Account? {
        val acs = AccountManager.get(con).accounts
        return if (acs.size > 1) { TooManyAccountsDialog().show(supportFragmentManager, DIALOGFRAGMENT_TAG)
            null
        } else if (acs.isEmpty()) {
            null
        } else {
            acs[0]
        }
    }
    fun GetAC(con: Context) : Account? {
        val acs = AccountManager.get(con).accounts
        return if (acs.size > 1 || acs.isEmpty()) {
            null
        } else {
            acs[0]
        }
    }
}