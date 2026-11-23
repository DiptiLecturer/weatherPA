package com.example.weatherapp_pa.ui.home.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp_pa.data.model.DailyWeather
import com.example.weatherapp_pa.databinding.ItemDailyForecastBinding

class DailyForecastAdapter(
    private val weatherList: List<DailyWeather>
) : RecyclerView.Adapter<DailyForecastAdapter.WeatherViewHolder>() {

    inner class WeatherViewHolder(
        val binding: ItemDailyForecastBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeatherViewHolder {

        val binding = ItemDailyForecastBinding.inflate(
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

            tvDay.text = weather.day

            tvDayCondition.text = weather.condition

            tvDayMax.text = weather.maxTemperature

            tvDayMin.text = weather.minTemperature

            Glide.with(ivDayIcon.context)
                .load(weather.iconUrl)
                .placeholder(android.R.drawable.ic_menu_day)
                .into(ivDayIcon)
        }
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }
}
