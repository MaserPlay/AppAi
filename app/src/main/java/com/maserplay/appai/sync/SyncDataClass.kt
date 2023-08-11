package com.maserplay.appai.sync

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage

class SyncDataClass {
    var model: String? = null
        private set
    @OptIn(BetaOpenAI::class)
    var content: MutableList<ChatMessage>? = null
        private set
    var token: String? = null
        private set
}