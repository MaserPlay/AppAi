package com.maserplay.appai

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type
import kotlin.collections.List as List

class ServiceDop(){
    private val FILE_NAME = "release"
    companion object {
        lateinit var fdir: File
        @OptIn(BetaOpenAI::class)
        lateinit var list: MutableList<ChatMessage>
    }
    init {}
    constructor(file: File) : this(){
        fdir = file}
    @OptIn(BetaOpenAI::class)
    fun GetList() : MutableList<ChatMessage> {Log.d("GetList", list.toString())
        return list
    }
    @OptIn(BetaOpenAI::class)
    fun add(ch: ChatMessage){
        list.add(ch)}
    @OptIn(BetaOpenAI::class)
    fun clear(){
        list.clear()}
    @OptIn(BetaOpenAI::class)
    fun openText() {
        if(!File(fdir, FILE_NAME).exists()) {
            list = mutableListOf()
            return
        }
        File(fdir, FILE_NAME).bufferedReader().use{ f ->
            val json=f.readText()
            val listType: Type = object : TypeToken<List<ChatMessage>>() {}.type
            list = GsonBuilder().create().fromJson(json, listType)
        }
    }
    @OptIn(BetaOpenAI::class)
    fun saveText() {
        val json= GsonBuilder().create().toJson(list)
        File(fdir, FILE_NAME).printWriter().use { out ->
            out.println(json)
        }
    }
}