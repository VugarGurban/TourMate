package com.gvugar.tourmate.retrofit.model.request

data class ChatRequestModel(val model: String, val messages: List<Message>)
{
    data class Message(val role: String, val content: String)
}