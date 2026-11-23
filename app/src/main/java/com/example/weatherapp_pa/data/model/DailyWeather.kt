package com.example.weatherapp_pa.data.model

data class DailyWeather(
    val day: String,
    val condition: String,
    val maxTemperature: String,
    val minTemperature: String,
    val iconUrl: String
)