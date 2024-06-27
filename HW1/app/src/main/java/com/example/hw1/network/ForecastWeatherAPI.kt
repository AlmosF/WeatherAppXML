package com.example.hw1.network

import com.example.hw1.model.forecast.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastWeatherAPI {
    @GET("/data/2.5/forecast")
    fun getWeather(
        @Query("q") cityName: String?,
        @Query("units") units: String?,
        @Query("cnt") cnt: Int?,
        @Query("appid") appId: String?
    ): Call<ForecastData?>?
}