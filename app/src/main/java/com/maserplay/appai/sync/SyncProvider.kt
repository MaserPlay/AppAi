package com.maserplay.appai.sync

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.maserplay.appai.GlobalVariables

class SyncProvider : ContentProvider(){
    override fun onCreate(): Boolean {
        Log.i(GlobalVariables.LOGTAG_SYNC, "onCreate_Provider")
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.i(GlobalVariables.LOGTAG_SYNC, "query")
        return null
    }

    override fun getType(uri: Uri): String? {
        Log.i(GlobalVariables.LOGTAG_SYNC, "getType")
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.i(GlobalVariables.LOGTAG_SYNC, "insert")
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.i(GlobalVariables.LOGTAG_SYNC, "delete")
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        Log.i(GlobalVariables.LOGTAG_SYNC, "update")
        return 0
    }
}