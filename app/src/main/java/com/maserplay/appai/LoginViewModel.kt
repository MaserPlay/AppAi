package com.maserplay.appai

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    val bool: MutableLiveData<Int> = MutableLiveData()
    lateinit var cookie: String
    var sum: Int = 0

}