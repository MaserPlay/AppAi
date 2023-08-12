package com.maserplay.appai.login

import android.accounts.AbstractAccountAuthenticator
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.accounts.NetworkErrorException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils


abstract class Account(val context: Context) : AbstractAccountAuthenticator(context) {

    @Throws(NetworkErrorException::class)
    override fun addAccount(
        response: AccountAuthenticatorResponse, accountType: String, authTokenType: String,
        requiredFeatures: Array<String>, options: Bundle?
    ): Bundle {
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra(LoginActivity.EXTRA_TOKEN_TYPE, accountType)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        val bundle = Bundle()
        if (options != null) {
            bundle.putAll(options)
        }
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }
    @Throws(NetworkErrorException::class)
    open fun getAuthToken(
        response: AccountAuthenticatorResponse?, account: Account, authTokenType: String?,
        options: Bundle?
    ): Bundle? {
        val result = Bundle()
        val am = AccountManager.get(context.applicationContext)
        var authToken = am.peekAuthToken(account, authTokenType)
        if (TextUtils.isEmpty(authToken)) {
            val password = am.getPassword(account)
            if (!TextUtils.isEmpty(password)) {
                authToken = AuthTokenLoader.signIn(context, account.name, password)
            }
        }
        if (!TextUtils.isEmpty(authToken)) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
        } else {
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
            intent.putExtra(LoginActivity.EXTRA_TOKEN_TYPE, authTokenType)
            val bundle = Bundle()
            bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        }
        return result
    }
}