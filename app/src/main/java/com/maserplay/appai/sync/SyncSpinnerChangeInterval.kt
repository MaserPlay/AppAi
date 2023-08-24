package com.maserplay.appai.sync

import android.content.ContentResolver
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.maserplay.appai.GlobalVariables

class SyncSpinnerChangeInterval : AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        val tr = view!!.context.getSharedPreferences(GlobalVariables.SHAREDPREFERENCES_NAME, AppCompatActivity.MODE_PRIVATE).getInt("sync_int", 86400)
        val shedit = view.context.getSharedPreferences(GlobalVariables.SHAREDPREFERENCES_NAME, AppCompatActivity.MODE_PRIVATE).edit()
        if (pos == 0){
            if (tr == 60) {return}
            shedit.putInt("sync_int", 60)
            ContentResolver.removePeriodicSync(GlobalVariables.GetAC(view.context), GlobalVariables.PROVIDER, Bundle.EMPTY)
            ContentResolver.addPeriodicSync(GlobalVariables.GetAC(view.context), GlobalVariables.PROVIDER, Bundle.EMPTY, 60)
        } else if (pos == 1){
            if (tr == 10800) {return}
            shedit.putInt("sync_int", 10800)
            ContentResolver.removePeriodicSync(GlobalVariables.GetAC(view.context), GlobalVariables.PROVIDER, Bundle.EMPTY)
            ContentResolver.addPeriodicSync(GlobalVariables.GetAC(view.context), GlobalVariables.PROVIDER, Bundle.EMPTY, 10800)
        } else if (pos == 2){
            if (tr == 86400) {return}
            shedit.putInt("sync_int", 86400)
            ContentResolver.removePeriodicSync(GlobalVariables.GetAC(view.context), GlobalVariables.PROVIDER, Bundle.EMPTY)
            ContentResolver.addPeriodicSync(GlobalVariables.GetAC(view.context), GlobalVariables.PROVIDER, Bundle.EMPTY, 86400)
        } else if (pos == 3){
            if (tr == 604800) {return}
            shedit.putInt("sync_int", 604800)
            ContentResolver.removePeriodicSync(GlobalVariables.GetAC(view.context), GlobalVariables.PROVIDER, Bundle.EMPTY)
            ContentResolver.addPeriodicSync(GlobalVariables.GetAC(view.context), GlobalVariables.PROVIDER, Bundle.EMPTY, 604800)
        }
        shedit.apply()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) { return }
}