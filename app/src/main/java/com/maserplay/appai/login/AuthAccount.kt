package com.maserplay.appai.login

import android.R.attr
import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.accounts.NetworkErrorException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.maserplay.AppAi.R
import com.maserplay.appai.GlobalVariables
import com.maserplay.appai.login.activity.LoginActivity


class AuthAccount(private val context: Context) : AbstractAccountAuthenticator(context) {
    override fun editProperties(
        response: AccountAuthenticatorResponse?,
        accountType: String?
    ): Bundle? {
        Log.i(GlobalVariables.LOGTAG_LOGIN, "editProperties")
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun addAccount(
        response: AccountAuthenticatorResponse?, accountType: String?, authTokenType: String?,
        requiredFeatures: Array<String?>?, options: Bundle?
    ): Bundle? {
        if (AccountManager.get(context).accounts.isNotEmpty()) {
            Toast.makeText(context, context.getString(R.string.error_toomany), Toast.LENGTH_LONG).show()
        return null}
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra(authTokenType, attr.accountType)
        intent.putExtra(accountType, attr.accountType)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    override fun confirmCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        options: Bundle?
    ): Bundle? {
        Log.i(GlobalVariables.LOGTAG_LOGIN, "confirmCredentials")
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle? {
        Log.i(GlobalVariables.LOGTAG_LOGIN, "getAuthToken")
        return null
    }

    override fun getAuthTokenLabel(authTokenType: String?): String? {
        Log.i(GlobalVariables.LOGTAG_LOGIN, "getAuthTokenLabel")
        return null
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle? {
        Log.i(GlobalVariables.LOGTAG_LOGIN, "updateCredentials")
        return null
    }

    override fun hasFeatures(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        features: Array<out String>?
    ): Bundle? {
        Log.i(GlobalVariables.LOGTAG_LOGIN, "hasFeatures")
        return null
    }
}