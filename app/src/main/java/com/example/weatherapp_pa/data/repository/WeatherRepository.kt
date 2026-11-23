package com.example.weatherapp_pa.data.repository

import com.example.weatherapp_pa.BuildConfig
import com.example.weatherapp_pa.data.remote.RetrofitClient

class WeatherRepository {

    private val api = RetrofitClient.api

    private val apiKey = BuildConfig.WEATHER_API_KEY

    // Get current weather

    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ) = api.getCurrentWeather(
        latitude = latitude,
        longitude = longitude,
        apiKey = apiKey
    )


    // Get five day forecast

    suspend fun getForecast(
        latitude: Double,
        longitude: Double
    ) = api.getForecast(
        latitude = latitude,
        longitude = longitude,
        apiKey = apiKey
    )


    // Search city or district

    suspend fun searchLocation(
        query: String
    ) = api.searchLocation(
        query = query,
        apiKey = apiKey
    )

}