package com.gvugar.tourmate.retrofit.builder

import com.gvugar.tourmate.retrofit.dao.ChatDao

object ApiUtils {
    fun getChatDao():ChatDao{
        return RetrofitClient.getInstance("https://api.openai.com/").create(ChatDao::class.java)

    }
}