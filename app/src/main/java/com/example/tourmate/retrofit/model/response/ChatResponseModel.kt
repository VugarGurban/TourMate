package com.example.tourmate.retrofit.model.response

data class ChatResponseModel(val choices: List<Choice>) {

    data class Choice(val message: Message)

    data class Message(val role: String, val content: String)

}