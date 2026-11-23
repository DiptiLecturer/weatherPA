package com.example.weatherapp_pa

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapp_pa.data.location.LocationProvider
import com.example.weatherapp_pa.data.model.DailyWeather
import com.example.weatherapp_pa.data.model.ForecastItem
import com.example.weatherapp_pa.data.model.HourlyWeather
import com.example.weatherapp_pa.data.model.LocationResponse
import com.example.weatherapp_pa.databinding.ActivityMainBinding
import com.example.weatherapp_pa.ui.home.WeatherViewModel
import com.example.weatherapp_pa.ui.home.WeatherViewModelFactory
import com.example.weatherapp_pa.ui.home.adapter.DailyForecastAdapter
import com.example.weatherapp_pa.ui.home.adapter.HourlyForecastAdapter
import com.example.weatherapp_pa.ui.home.adapter.LocationSearchAdapter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WeatherViewModel
    private lateinit var locationProvider: LocationProvider

    private lateinit var searchAdapter: LocationSearchAdapter



    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val granted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                getDeviceLocation()
            } else {
                Toast.makeText(
                    this,
                    "Location permission is required to detect your location.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun checkLocationPermission() {

        val fineLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocation || coarseLocation) {

            getDeviceLocation()

        } else {

            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getDeviceLocation() {

        locationProvider = LocationProvider(this)

        lifecycleScope.launch {

            try {

                val location =
                    locationProvider.getCurrentLocation()

                if (location != null) {

                    val latitude = location.latitude
                    val longitude = location.longitude

                    Toast.makeText(
                        this@MainActivity,
                        "Location: $latitude, $longitude",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Fetch weather using actual GPS coordinates

                    viewModel.fetchWeather(
                        latitude,
                        longitude
                    )

                } else {

                    Toast.makeText(
                        this@MainActivity,
                        "Unable to get location. Check GPS settings.",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: SecurityException) {

                Toast.makeText(
                    this@MainActivity,
                    "Location permission denied.",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {

                Toast.makeText(
                    this@MainActivity,
                    "Location error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setupLocationSearch() {

        // Initialize adapter
        searchAdapter = LocationSearchAdapter { location ->

            selectLocation(location)

        }

        // Setup RecyclerView
        binding.rvSearchResults.apply {

            layoutManager = LinearLayoutManager(this@MainActivity)

            adapter = searchAdapter
        }

        // Listen to search input
        binding.etSearch.addTextChangedListener(

            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    viewModel.searchLocations(s.toString())

                    if (s.isNullOrBlank()) {

                        binding.rvSearchResults.visibility = View.GONE

                    }
                }

                override fun afterTextChanged(s: Editable?) {}

            }
        )

        // Observe search results
        viewModel.searchResults.observe(this) { locations ->

            searchAdapter.updateLocations(locations)

            binding.rvSearchResults.visibility =

                if (locations.isNotEmpty() &&
                    binding.etSearch.hasFocus()
                ) {

                    View.VISIBLE

                } else {

                    View.GONE

                }
        }

        // Current location button
        binding.btnCurrentLocation.setOnClickListener {

            binding.etSearch.text.clear()

            checkLocationPermission()

        }
    }


    @SuppressLint("SetTextI18n")
    private fun selectLocation(location: LocationResponse) {

        // Hide keyboard
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(
            binding.etSearch.windowToken,
            0
        )

        // Remove focus
        binding.etSearch.clearFocus()

        // Hide search suggestions
        binding.rvSearchResults.visibility = View.GONE

        // Show selected location
        binding.etSearch.setText(
            "${location.name}, ${location.country}"
        )

        // Fetch weather using selected coordinates
        val latitude = location.lat
        val longitude = location.lon

        if (latitude != null && longitude != null) {

            viewModel.fetchWeather(
                latitude,
                longitude
            )

        } else {

            Toast.makeText(
                this,
                "Invalid location coordinates",
                Toast.LENGTH_SHORT
            ).show()

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Initialize ViewModel
        setupViewModel()

        // Observe weather API responses
        observeWeather()

        // Initialize search functionality
        setupLocationSearch()

        // Request GPS permission
        checkLocationPermission()

    }

    private fun setupViewModel() {

        val factory = WeatherViewModelFactory()

        viewModel = ViewModelProvider(
            this,
            factory
        )[WeatherViewModel::class.java]
    }

    @SuppressLint("SetTextI18n")
    private fun observeWeather() {
        viewModel.forecast.observe(this) { forecast ->

            val forecastList = forecast.list ?: emptyList()

            // Display hourly forecast
            showHourlyForecast(forecastList)

            // Display daily forecast
            showDailyForecast(forecastList)
        }

        // Observe current weather

        viewModel.weather.observe(this) { weather ->

            binding.apply {

                tvCity.text =
                    "${weather.name ?: "Unknown"}, ${weather.sys?.country ?: ""}"

                tvTemperature.text =
                    "${weather.main?.temp?.toInt() ?: "--"}°"

                tvFeelsLike.text =
                    "Feels like ${weather.main?.feels_like?.toInt() ?: "--"}°"

                tvHighLow.text =
                    "H: ${weather.main?.temp_max?.toInt() ?: "--"}°   " +
                            "L: ${weather.main?.temp_min?.toInt() ?: "--"}°"

                tvHumidity.text =
                    "${weather.main?.humidity ?: "--"}%"

                tvWind.text =
                    "${((weather.wind?.speed ?: 0.0) * 3.6).toInt()} km/h"

                tvPressure.text =
                    "${weather.main?.pressure ?: "--"} hPa"

                tvVisibility.text =
                    "${(weather.visibility ?: 0) / 1000} km"

                tvWeatherCondition.text =
                    weather.weather?.firstOrNull()?.description
                        ?.replaceFirstChar { it.uppercase() } ?: "Unknown"

                // Load weather icon with Glide

                val icon = weather.weather?.firstOrNull()?.icon

                if (icon != null) {

                    val iconUrl =
                        "https://openweathermap.org/img/wn/${icon}@4x.png"

                    Glide.with(this@MainActivity)
                        .load(iconUrl)
                        .into(ivWeatherIcon)
                }
            }
        }

        // Observe loading

        viewModel.loading.observe(this) { isLoading ->

            binding.progressBar.visibility =
                if (isLoading) android.view.View.VISIBLE
                else android.view.View.GONE
        }

        // Observe errors

        viewModel.error.observe(this) { message ->

            if (message.isNotEmpty()) {

                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun showDailyForecast(
        forecastList: List<ForecastItem>
    ) {

        // Group forecast records by date

        val groupedForecast = forecastList
            .filter { !it.dt_txt.isNullOrBlank() }
            .groupBy { item ->
                item.dt_txt!!.substring(0, 10)
            }

        // Convert each date into DailyWeather

        val dailyList = groupedForecast
            .entries
            .take(5)
            .map { entry ->

                val dayRecords = entry.value

                // Find maximum temperature

                val maxTemp = dayRecords.maxOfOrNull {
                    it.main?.temp_max ?: it.main?.temp ?: Double.NEGATIVE_INFINITY
                } ?: 0.0

                // Find minimum temperature

                val minTemp = dayRecords.minOfOrNull {
                    it.main?.temp_min ?: it.main?.temp ?: Double.POSITIVE_INFINITY
                } ?: 0.0

                // Get weather condition

                val weatherInfo = dayRecords
                    .firstNotNullOfOrNull {
                        it.weather?.firstOrNull()
                    }

                val condition = weatherInfo?.description
                    ?.replaceFirstChar { it.uppercase() }
                    ?: "Unknown"

                val icon = weatherInfo?.icon ?: "01d"

                // Convert date into day name

                val day = try {

                    val inputFormat = SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    )

                    val outputFormat = SimpleDateFormat(
                        "EEEE",
                        Locale.getDefault()
                    )

                    val date = inputFormat.parse(entry.key)

                    date?.let { outputFormat.format(it) } ?: entry.key

                } catch (e: Exception) {
                    entry.key
                }

                DailyWeather(
                    day = day,
                    condition = condition,
                    maxTemperature = "${maxTemp.toInt()}°",
                    minTemperature = "${minTemp.toInt()}°",
                    iconUrl = "https://openweathermap.org/img/wn/${icon}@2x.png"
                )
            }

        // Display RecyclerView

        binding.rvDailyForecast.layoutManager =
            LinearLayoutManager(this)

        binding.rvDailyForecast.adapter =
            DailyForecastAdapter(dailyList)
    }



    private fun showHourlyForecast(
        forecastList: List<ForecastItem>
    ) {

        val hourlyList = forecastList.take(8).map { item ->

            val time = try {

                val inputFormat = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                )

                val outputFormat = SimpleDateFormat(
                    "hh a",
                    Locale.getDefault()
                )

                val date = inputFormat.parse(
                    item.dt_txt.orEmpty()
                )

                date?.let {
                    outputFormat.format(it)
                } ?: "--"

            } catch (e: Exception) {

                "--"

            }

            val temperature =
                "${item.main?.temp?.toInt() ?: 0}°"

            val icon = item.weather
                ?.firstOrNull()
                ?.icon ?: "01d"

            val iconUrl =
                "https://openweathermap.org/img/wn/${icon}@2x.png"

            HourlyWeather(
                time = time,
                temperature = temperature,
                iconUrl = iconUrl
            )

        }

        // Setup horizontal RecyclerView
        binding.rvHourlyForecast.apply {

            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            adapter = HourlyForecastAdapter(hourlyList)

        }
    }


}