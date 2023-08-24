package com.maserplay.appai.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.maserplay.appai.GlobalVariables

class SyncService : Service() {
    private var sSyncAdapter: SyncAdapter? = null
    private val sSyncAdapterLock = Any()
    override fun onCreate() {
        Log.i(GlobalVariables.LOGTAG_SYNC, "onCreate")
        synchronized(sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = SyncAdapter(applicationContext, true)
            }
        }
    }
    override fun onBind(intent: Intent?): IBinder? {
        Log.i(GlobalVariables.LOGTAG_SYNC, "onBind")
        return sSyncAdapter!!.syncAdapterBinder
    }

}