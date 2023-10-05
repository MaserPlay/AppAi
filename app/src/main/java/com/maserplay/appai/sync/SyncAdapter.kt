package com.maserplay.appai.sync

import android.accounts.Account
import android.accounts.AccountManager
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SharedPreferences
import android.content.SyncResult
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.aallam.openai.api.BetaOpenAI
import com.maserplay.appai.GlobalVariables
import com.maserplay.appai.ServiceDop
import com.maserplay.appai.Web
import com.maserplay.appai.login.send_get_classes.LoginCheckTokenClass
import com.maserplay.appai.login.send_get_classes.LoginVerifyClass
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SyncAdapter(val con: Context, autoini: Boolean) : AbstractThreadedSyncAdapter(con, autoini) {
    override fun onPerformSync(
        account: Account,
        extras: Bundle?,
        authority: String?,
        provider: ContentProviderClient,
        syncResult: SyncResult
    ) {
        Log.i(GlobalVariables.LOGTAG_SYNC, "onPerformSync")
        val shpref = con.getSharedPreferences(
            GlobalVariables.SHAREDPREFERENCES_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        val token = AccountManager.get(con).blockingGetAuthToken(account, "cookie", true)
        runBlocking {
            Sync(token, shpref, syncResult, account)
        }
    }

    @OptIn(BetaOpenAI::class)
    private suspend fun Sync(
        token: String,
        shpref: SharedPreferences,
        syncResult: SyncResult,
        ac: Account
    ) {
        val check = SendCheck(LoginCheckTokenClass(token))
        if (!check.isSuccessful) {
            syncResult.stats.numIoExceptions++
            return
        }
        if (check.body()!!.verify == false) {
            AccountManager.get(con).removeAccountExplicitly(ac)
            return
        }
        val get = Send(
            SyncDataClass(
                token,
                shpref.getString("gptver", "gpt-3.5-turbo")!!,
                ServiceDop.GetList(),
                shpref.getString("api", "")!!,
                shpref.getString("lastupdate", "gpt-3.5-turbo")!!
            )
        )
        if (!get.isSuccessful) {
            syncResult.stats.numIoExceptions++
            return
        }
        val getbody = get.body()
        val put = shpref.edit()
        ServiceDop.SetList(getbody!!.content)
        put.putString("gptver", getbody.model)
        put.putString("api", getbody.token)
        put.apply()
    }

    private suspend fun Send(send: SyncDataClass): Response<SyncDataClass> {
        return Retrofit.Builder()
            .baseUrl(GlobalVariables.WEB_ADR_FULL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Web::class.java).sync(send)
    }

    private suspend fun SendCheck(send: LoginCheckTokenClass): Response<LoginVerifyClass> {
        return Retrofit.Builder()
            .baseUrl(GlobalVariables.WEB_ADR_FULL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Web::class.java).checktoken(send)
    }
}