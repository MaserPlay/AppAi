package com.maserplay.appai.login

import kotlin.properties.Delegates

class LoginAcceptClass {
    var emailaccept: Boolean? = null
        private set
    var totp: Boolean? = null
        private set

    override fun toString(): String {
        return "LoginAcceptClass(emailaccept=$emailaccept, totp=$totp)"
    }


}