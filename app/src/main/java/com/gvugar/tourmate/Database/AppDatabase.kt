package com.gvugar.tourmate.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gvugar.tourmate.dao.CitiesDao
import com.gvugar.tourmate.dao.PlacesDao
import com.gvugar.tourmate.entities.CitiesModel
import com.gvugar.tourmate.entities.PlacesModel

@Database(entities = [CitiesModel::class, PlacesModel::class] , version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun citiesDao(): CitiesDao
    abstract fun placesDao(): PlacesDao

    companion object{
        const val DATABASE_NAME = "my_app_db.db"
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }

}