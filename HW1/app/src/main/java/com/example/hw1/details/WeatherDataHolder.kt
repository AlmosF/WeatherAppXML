package com.example.hw1.details

import com.example.hw1.model.WeatherData

interface WeatherDataHolder {
    fun getWeatherData(): WeatherData?
}