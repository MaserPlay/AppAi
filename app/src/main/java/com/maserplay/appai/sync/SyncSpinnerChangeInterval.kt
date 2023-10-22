package com.maserplay.appai.sync

import android.content.ContentResolver
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.maserplay.appai.GlobalVariables
import com.maserplay.loginlib.GlobalVariables as loglvar

class SyncSpinnerChangeInterval : AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        val tr = parent!!.context.getSharedPreferences(
            GlobalVariables.SHAREDPREFERENCES_NAME,
            AppCompatActivity.MODE_PRIVATE
        ).getInt("sync_int", 86400)
        val shedit = parent.context.getSharedPreferences(
            GlobalVariables.SHAREDPREFERENCES_NAME,
            AppCompatActivity.MODE_PRIVATE
        ).edit()
        when (pos) {
            0 -> {
                if (tr == 60) {
                    return
                }
                shedit.putInt("sync_int", 60)
                ContentResolver.addPeriodicSync(
                    loglvar.GetAC(parent.context),
                    GlobalVariables.PROVIDER,
                    Bundle.EMPTY,
                    60
                )
            }
            1 -> {
                if (tr == 10800) {
                    return
                }
                shedit.putInt("sync_int", 10800)
                ContentResolver.addPeriodicSync(
                    loglvar.GetAC(parent.context),
                    GlobalVariables.PROVIDER,
                    Bundle.EMPTY,
                    10800
                )
            }
            2 -> {
                if (tr == 86400) {
                    return
                }
                shedit.putInt("sync_int", 86400)
                ContentResolver.addPeriodicSync(
                    loglvar.GetAC(parent.context),
                    GlobalVariables.PROVIDER,
                    Bundle.EMPTY,
                    86400
                )
            }
            3 -> {
                if (tr == 604800) {
                    return
                }
                ContentResolver.addPeriodicSync(
                    loglvar.GetAC(parent.context),
                    GlobalVariables.PROVIDER,
                    Bundle.EMPTY,
                    604800
                )
            }
        }
        shedit.apply()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        return
    }
}