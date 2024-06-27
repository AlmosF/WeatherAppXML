package com.example.hw1.model

data class MainWeatherData (
    val temp: Float = 0f,
    val feels_like: Float = 0f,
    val temp_min: Float = 0f,
    val temp_max: Float = 0f,
    val pressure: Float = 0f,
    val humidity: Float = 0f,
    val sea_level: Float = 0f,
    val grnd_level: Float = 0f
)