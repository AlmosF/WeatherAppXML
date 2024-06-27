package com.example.hw1.network

import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.preference.PreferenceManager
import com.example.hw1.model.WeatherData
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkManager {
    private val retrofit: Retrofit
    private val weatherApi: WeatherAPI

    private const val SERVICE_URL = "https://api.openweathermap.org"
    private const val APP_ID = "7d5b2c489ea465597e9d116aed1ca800"

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(SERVICE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        weatherApi = retrofit.create(WeatherAPI::class.java)
    }

    fun getWeather(city: String?, context : Context): Call<WeatherData?>? {
        val isMetric = PreferenceManager.getDefaultSharedPreferences(context).getString("unit_preference_title", "Metric") == "Metric"
        //Log.w("NetworkManager", isMetric.toString())
        return when(isMetric){
            true -> weatherApi.getWeather(city, "metric", APP_ID)
            false -> weatherApi.getWeather(city, "imperial", APP_ID)
        }
    }

    fun getWeatherByCoord(lat: Double?, lon: Double?, context : Context): Call<WeatherData?>? {
        val isMetric = PreferenceManager.getDefaultSharedPreferences(context).getString("unit_preference_title", "Metric") == "Metric"
        //Log.w("NetworkManager", isMetric.toString())
        return when(isMetric){
            true -> weatherApi.getWeatherByCoord(lat, lon, "metric", APP_ID)
            false -> weatherApi.getWeatherByCoord(lat, lon, "imperial", APP_ID)
        }
    }
}