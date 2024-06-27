package com.example.hw1.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class City(
    @PrimaryKey(autoGenerate = true) var CityId: Long?,
    @ColumnInfo(name = "CityName") var CityName: String,
    @ColumnInfo(name = "CountryName") var CountryName: String,
    @ColumnInfo(name = "Temperature") var Temperature: String,
    @ColumnInfo(name = "Image") var Image: String,
)