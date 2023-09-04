package com.maserplay.appai.sync

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage

@OptIn(BetaOpenAI::class)
class SyncDataClass constructor(_cookie: String, _model: String, _content: MutableList<ChatMessage>, _token: String, _time: String) {
    private var cookie: String? = null
    lateinit var model: String
    lateinit var content: MutableList<ChatMessage>
    lateinit var token: String
    private var time: String? = null
    init {
        cookie = _cookie
        model = _model
        content = _content
        token = _token
        time = _time
    }
}