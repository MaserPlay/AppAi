package com.maserplay.appai.login

import kotlin.properties.Delegates

class LoginAcceptClass {
    private var emailaccept by Delegates.notNull<Boolean>()
    private var totp by Delegates.notNull<Boolean>()
}