package com.example.tourmate.retrofit.builder

import com.example.tourmate.retrofit.dao.ChatDao

object ApiUtils {
    fun getChatDao():ChatDao{
        return RetrofitClient.getInstance("https://api.openai.com/").create(ChatDao::class.java)

    }
}