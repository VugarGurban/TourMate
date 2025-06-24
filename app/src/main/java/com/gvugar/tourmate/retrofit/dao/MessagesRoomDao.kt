package com.gvugar.tourmate.retrofit.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gvugar.tourmate.entities.ChatMessagesModel

@Dao
interface MessagesRoomDao {

    @Insert
    suspend fun insertMessage(message: ChatMessagesModel)

    @Query("SELECT * FROM chat_messages")
    suspend fun getAllMessages(): List<ChatMessagesModel>

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllMessages()

}