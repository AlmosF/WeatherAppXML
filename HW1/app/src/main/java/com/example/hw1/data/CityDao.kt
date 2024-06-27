package com.example.hw1.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CityDao {

    @Query("SELECT * FROM city")
    fun getAllCityItems(): List<City>

    @Insert
    fun insertCityItem(city: City) : Long

    @Delete
    fun deleteCityItem(city: City)

    @Update
    fun updateCityItem(city: City)

}