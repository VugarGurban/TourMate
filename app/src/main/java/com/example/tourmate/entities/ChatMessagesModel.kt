package com.example.tourmate.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessagesModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val message: String,
    val isSentByUser: Boolean
)
