package com.example.tourmate.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tourmate.entities.PlacesModel

@Dao
interface PlacesDao {
    @Insert
    fun addPlace(place: PlacesModel)

    @Update
    fun updatePlace(place: PlacesModel)

    @Delete
    fun deletePlace(place: PlacesModel)

    @Query("SELECT * FROM places")
    fun getAllPlaces(): List<PlacesModel>

    @Query("SELECT * FROM places WHERE id = :id")
    fun getPlace(id: Int): PlacesModel
}