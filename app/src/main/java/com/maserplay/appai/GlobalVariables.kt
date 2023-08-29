package com.maserplay.appai

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import androidx.fragment.app.FragmentManager
import com.maserplay.appai.dialogfragment.TooManyAccountsDialog

object GlobalVariables{
    const val WEB_ADR_FULL = "https://games.m2023.ru/"
    const val WEB_NAME = "games.m2023.ru"
    const val SHAREDPREFERENCES_NAME = "Main"
    const val SHAREDPREFERENCES_DEBUG = "debug"
    const val GPTVER_DEFVAl = "gpt-3.5-turbo"
    const val DIALOGFRAGMENT_TAG = "error"
    const val APP_NAME = "AppAi"
    const val LOGTAG_SYNC = "SyncTAG"
    const val LOGTAG_LOGIN = "LoginTAG"
    const val PROVIDER = "com.maserplay.appai.sync.Provider"
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