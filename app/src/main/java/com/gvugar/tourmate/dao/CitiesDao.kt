package com.gvugar.tourmate.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gvugar.tourmate.entities.CitiesModel

@Dao
interface CitiesDao {
    @Insert
    fun addCity(city: CitiesModel)

    @Update
    fun updateCity(city: CitiesModel)

    @Delete
    fun deleteCity(city: CitiesModel)

    @Query("SELECT * FROM cities")
    fun getAllCities(): List<CitiesModel>

    @Query("SELECT * FROM cities WHERE id = :id")
    fun getCity(id: Int): CitiesModel

}