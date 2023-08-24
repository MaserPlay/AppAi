package com.maserplay.appai.login.send_get_classes

import kotlin.properties.Delegates

class LoginVerifyClass {
    var verify by Delegates.notNull<Boolean>()
        private set

    override fun toString(): String {
        return "LoginVerifyClass(verify=$verify)"
    }

}