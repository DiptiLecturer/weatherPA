package com.example.weatherapp_pa.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider

import com.example.weatherapp_pa.data.model.WeatherResponse
import com.example.weatherapp_pa.data.model.ForecastResponse
import com.example.weatherapp_pa.data.model.LocationResponse
import com.example.weatherapp_pa.data.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds

class WeatherViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {

            return WeatherViewModel(
                WeatherRepository()
            ) as T

        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {
    private val _searchResults =
        MutableLiveData<List<LocationResponse>>()

    val searchResults: LiveData<List<LocationResponse>>
        get() = _searchResults


    private var searchJob: Job? = null
    // Current weather data

    private val _weather = MutableLiveData<WeatherResponse>()

    fun searchLocations(query: String) {

        searchJob?.cancel()

        if (query.trim().length < 2) {
            _searchResults.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {

            delay(500.milliseconds)

            try {

                val results =
                    repository.searchLocation(query.trim())

                _searchResults.value = results

            } catch (e: Exception) {

                _searchResults.value = emptyList()
            }
        }
    }
    val weather: LiveData<WeatherResponse>
        get() = _weather


    // Forecast data

    private val _forecast = MutableLiveData<ForecastResponse>()

    val forecast: LiveData<ForecastResponse>
        get() = _forecast


    // Loading status

    private val _loading = MutableLiveData<Boolean>()

    val loading: LiveData<Boolean>
        get() = _loading


    // Error message

    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error


    // Fetch weather information

    fun fetchWeather(
        latitude: Double,
        longitude: Double
    ) {

        viewModelScope.launch {

            _loading.value = true
            _error.value = ""

            try {

                // Current weather

                val currentWeather =
                    repository.getCurrentWeather(
                        latitude,
                        longitude
                    )

                _weather.value = currentWeather


                // Five day forecast

                val forecastData =
                    repository.getForecast(
                        latitude,
                        longitude
                    )

                _forecast.value = forecastData

            } catch (e: IOException) {

                _error.value =
                    "Please check your internet connection."

            } catch (e: HttpException) {

                _error.value =
                    "API error: ${e.code()} ${e.message()}"

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Something went wrong."

            } finally {

                _loading.value = false

            }
        }
    }
}