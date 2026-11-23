package com.example.weatherapp_pa.data.remote

import com.example.weatherapp_pa.data.model.ForecastResponse
import com.example.weatherapp_pa.data.model.LocationResponse
import com.example.weatherapp_pa.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    // Current weather API

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(

        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String

    ): WeatherResponse


    // Five day forecast API

    @GET("data/2.5/forecast")
    suspend fun getForecast(

        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String

    ): ForecastResponse


    // Search city or district

    @GET("geo/1.0/direct")
    suspend fun searchLocation(

        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String

    ): List<LocationResponse>

}