package com.maserplay.loginlib

data class RegisterClass(val login: String, val password: String, val nickname: String)
data class RegisterResponseClass(val success: Boolean, val error: String)
data class LoginClass(val login: String, val password: String)
data class LoginUserClass(val login: String, val admin: Boolean)
data class LoginResponseClass(val user: LoginUserClass, val success: Boolean, val error: String)