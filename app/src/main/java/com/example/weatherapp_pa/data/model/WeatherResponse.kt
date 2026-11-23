package com.example.weatherapp_pa.data.model

data class WeatherResponse(
    val name: String?,
    val main: MainWeather?,
    val weather: List<WeatherInfo>?,
    val wind: WindInfo?,
    val visibility: Int?,
    val sys: SysInfo?,
    val dt: Long?
)

data class MainWeather(
    val temp: Double?,
    val feels_like: Double?,
    val temp_min: Double?,
    val temp_max: Double?,
    val humidity: Int?,
    val pressure: Int?
)

data class WeatherInfo(
    val main: String?,
    val description: String?,
    val icon: String?
)

data class WindInfo(
    val speed: Double?
)

data class SysInfo(
    val country: String?,
    val sunrise: Long?,
    val sunset: Long?
)