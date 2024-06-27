package com.example.hw1.model.forecast

data class ForecastData(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<WeatherForecast>,
    val message: Int
)