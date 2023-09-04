package com.maserplay.appai.login.send_get_classes

class LoginVerifySendClass(_acceptcookie: String, _code: Int) {
    private var acceptcookie: String
    private val code: String

    init {
        acceptcookie = _acceptcookie
        code = _code.toString()
    }
    constructor(_acceptcookie: String) : this(_acceptcookie, _code = 0) {
        this.acceptcookie = _acceptcookie
    }
}