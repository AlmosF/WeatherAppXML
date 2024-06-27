package com.example.hw1.network

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.example.hw1.model.forecast.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ForecastNertworkManager {
    private val retrofit: Retrofit
    private val forecastWeatherAPI: ForecastWeatherAPI

    private const val SERVICE_URL = "https://api.openweathermap.org"
    private const val APP_ID = "7d5b2c489ea465597e9d116aed1ca800"

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(SERVICE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        forecastWeatherAPI = retrofit.create(ForecastWeatherAPI::class.java)
    }

    fun getWeather(city: String?, context : Context): Call<ForecastData?>? {
        val isMetric = PreferenceManager.getDefaultSharedPreferences(context).getString("unit_preference_title", "Metric") == "Metric"
        //Log.w("NetworkManager", isMetric.toString())
        return when(isMetric){
            true -> forecastWeatherAPI.getWeather(city, "metric", 7, APP_ID)
            false -> forecastWeatherAPI.getWeather(city, "imperial",7,  APP_ID)
        }
    }
}