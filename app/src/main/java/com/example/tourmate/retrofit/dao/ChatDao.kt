package com.example.tourmate.retrofit.dao

import com.example.tourmate.retrofit.model.request.ChatRequestModel
import com.example.tourmate.retrofit.model.response.ChatResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatDao {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer " // + API_KEY
    )
    @POST("v1/chat/completions")
    fun sendMessage(@Body request: ChatRequestModel): Call<ChatResponseModel>
}