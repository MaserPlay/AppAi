package com.maserplay.appai.sync

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage

@OptIn(BetaOpenAI::class)
class SyncDataClass constructor(_cookie: String, _model: String, _content: MutableList<ChatMessage>, _token: String) {
    var cookie: String? = null
    var model: String? = null
    var content: MutableList<ChatMessage>? = null
    var token: String? = null
    init {
        cookie = _cookie
        model = _model
        content = _content
        token = _token
    }
}