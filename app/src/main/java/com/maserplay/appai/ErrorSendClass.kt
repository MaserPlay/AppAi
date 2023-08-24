package com.maserplay.appai

import android.os.Build

class ErrorSendClass(_mess: String) {
    private val AppName: String
    private val AppVersion: String
    private val Message: String

    init {
        Message = _mess
        AppVersion = Build.VERSION.RELEASE
        AppName = GlobalVariables.APP_NAME
    }

}
