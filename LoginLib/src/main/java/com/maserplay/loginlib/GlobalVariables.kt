package com.maserplay.loginlib

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import androidx.fragment.app.FragmentManager
import com.maserplay.loginlib.dialogfragment.AccountLogoutDialog
import com.maserplay.loginlib.dialogfragment.TooManyAccountsDialog
import java.util.concurrent.TimeUnit

object GlobalVariables {
    const val WEB_ADR_FULL = "https://games.m2023.ru/"

    //const val WEB_ADR_FULL = "http://localhost:5000/"
    const val DIALOGFRAGMENT_TAG = "error"
    const val LOGTAG_LOGIN = "LoginTAG"
    fun GetAC(supportFragmentManager: FragmentManager, con: Context): Account? {
        val acs = AccountManager.get(con).accounts
        return if (acs.size > 1) {
            TooManyAccountsDialog().show(supportFragmentManager, DIALOGFRAGMENT_TAG)
            null
        } else if (acs.isEmpty()) {
            null
        } else {
            var ac = acs[0]
            if (isAcValid(supportFragmentManager, ac, con)) {
                ac
            } else {
                null
            }
        }
    }

    fun GetAC(con: Context): Account? {
        val acs = AccountManager.get(con).accounts
        return if (acs.size > 1 || acs.isEmpty()) {
            null
        } else {
            var ac = acs[0]
            if (isAcValid(ac, con)) {
                ac
            } else {
                null
            }
        }
    }

    fun isAcValid(
        supportFragmentManager: FragmentManager,
        account: Account,
        con: Context
    ): Boolean {
        if (!isAcValid(account, con)) {
            AccountLogoutDialog().show(supportFragmentManager, DIALOGFRAGMENT_TAG)
            return false
        }
        return true
    }

    fun isAcValid(account: Account, con: Context): Boolean {
        val curtimeSeconds: Long = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        val acexptime = AccountManager.get(con).getUserData(account, "exp").toLong()
        if (curtimeSeconds > acexptime) {
            logout(account, con)
            return false
        }
        return true
    }

    fun logout(account: Account, con: Context) {
        AccountManager.get(con).removeAccountExplicitly(account)
    }
}