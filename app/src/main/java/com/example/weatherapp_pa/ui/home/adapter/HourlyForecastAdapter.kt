package com.example.weatherapp_pa.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp_pa.data.model.HourlyWeather
import com.example.weatherapp_pa.databinding.ItemHourlyForecastBinding

class HourlyForecastAdapter(

    private val weatherList: List<HourlyWeather>
) : RecyclerView.Adapter<HourlyForecastAdapter.WeatherViewHolder>() {

    inner class WeatherViewHolder(
        val binding: ItemHourlyForecastBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeatherViewHolder {

        val binding = ItemHourlyForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: WeatherViewHolder,
        position: Int
    ) {

        val weather = weatherList[position]

        holder.binding.apply {

            tvHour.text = weather.time

            tvHourTemp.text = weather.temperature

            Glide.with(ivHourIcon.context)
                .load(weather.iconUrl)
                .placeholder(android.R.drawable.ic_menu_day)
                .into(ivHourIcon)
        }
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }
}