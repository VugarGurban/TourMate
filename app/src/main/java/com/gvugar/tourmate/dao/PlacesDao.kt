package com.gvugar.tourmate.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gvugar.tourmate.entities.PlacesModel

@Dao
interface PlacesDao {
    @Insert
    fun addPlace(place: PlacesModel)

    @Update
    fun updatePlace(place: PlacesModel)

    @Delete
    fun deletePlace(place: PlacesModel)

    @Query("SELECT * FROM places")
    fun getAllPlaces(): MutableList<PlacesModel>

    @Query("SELECT * FROM places WHERE id = :id")
    fun getPlaceById(id: Int): PlacesModel

    @Query("SELECT * FROM places WHERE city_id = :id")
    fun getPlaceByCityId(id: Int): MutableList<PlacesModel>

    @Query("SELECT * FROM places WHERE category_id = :id and city_id = :cityId")
    fun getPlaceByCategory(id: Int, cityId: Int): MutableList<PlacesModel>

    @Query("UPDATE places SET is_liked = :newValue WHERE id = :id")
    fun updateIsLiked(id: Int, newValue: Int)

    @Query("SELECT * FROM places WHERE is_liked = 1")
    fun getLikedPlaces(): MutableList<PlacesModel>

}