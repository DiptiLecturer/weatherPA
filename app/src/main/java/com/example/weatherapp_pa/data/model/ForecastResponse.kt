package com.example.weatherapp_pa.data.model

data class ForecastResponse(
    val list: List<ForecastItem>?,
    val city: ForecastCity?
)

data class ForecastItem(
    val dt: Long?,
    val dt_txt: String?,
    val main: MainWeather?,
    val weather: List<WeatherInfo>?,
    val wind: WindInfo?,
    val pop: Double?
)

data class ForecastCity(
    val name: String?,
    val country: String?,
    val timezone: Int?,
    val sunrise: Long?,
    val sunset: Long?
)
