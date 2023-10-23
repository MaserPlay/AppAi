package com.maserplay.appai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type
import kotlin.collections.List as List

object ServiceDop {
    private val FILE_NAME = "release"
    private lateinit var fdir: File

    @OptIn(BetaOpenAI::class)
    private lateinit var list: MutableList<ChatMessage>
    fun start(file: File) {
        fdir = file
    }

    @OptIn(BetaOpenAI::class)
    fun GetList(): MutableList<ChatMessage> {
        return list
    }

    @OptIn(BetaOpenAI::class)
    fun SetList(m: MutableList<ChatMessage>){
        list = m
    }

    @OptIn(BetaOpenAI::class)
    fun add(ch: ChatMessage) {
        list.add(ch)
    }

    @OptIn(BetaOpenAI::class)
    fun clear() {
        list.clear()
    }

    @OptIn(BetaOpenAI::class)
    fun openText() {
        if (!File(fdir, FILE_NAME).exists()) {
            list = mutableListOf()
            return
        }
        File(fdir, FILE_NAME).bufferedReader().use { f ->
            val json = f.readText()
            if (json == "") {
                list = mutableListOf()
                return
            }
            val listType: Type = object : TypeToken<List<ChatMessage>>() {}.type
            list = GsonBuilder().create().fromJson(json, listType)
        }
    }

    @OptIn(BetaOpenAI::class)
    fun saveText() {
        val json = GsonBuilder().create().toJson(list)
        File(fdir, FILE_NAME).printWriter().use { out ->
            out.println(json)
        }
    }
}