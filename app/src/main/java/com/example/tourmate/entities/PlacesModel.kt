package com.example.tourmate.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlacesModel (
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "city_id")
    var city_id: Int,

    @ColumnInfo(name = "category_id")
    var category_id: Int,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "address")
    var address: String,

    @ColumnInfo(name = "rate")
    var rate: Float,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "link")
    var link: String
)