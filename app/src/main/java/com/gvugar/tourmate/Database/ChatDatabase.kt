package com.gvugar.tourmate.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gvugar.tourmate.entities.ChatMessagesModel
import com.gvugar.tourmate.retrofit.dao.MessagesRoomDao

@Database(entities = [ChatMessagesModel::class], version = 1)
abstract class ChatDatabase: RoomDatabase() {
    abstract fun messagesRoomDao(): MessagesRoomDao

    companion object{
        const val DATABASE_NAME = "chat_messages"

        private var INSTANCE: ChatDatabase? = null

        fun getInstance(context: Context): ChatDatabase {
            return ChatDatabase.INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    ChatDatabase.DATABASE_NAME
                ).build()
                ChatDatabase.INSTANCE = instance
                instance
            }
        }

    }
}