package com.example.weatherapp_pa.ui.home.adapter



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp_pa.data.model.LocationResponse
import com.example.weatherapp_pa.databinding.ItemLocationSearchBinding

class LocationSearchAdapter(
    private val onLocationClick: (LocationResponse) -> Unit
) : RecyclerView.Adapter<LocationSearchAdapter.LocationViewHolder>() {

    private val locations = mutableListOf<LocationResponse>()

    inner class LocationViewHolder(
        val binding: ItemLocationSearchBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocationViewHolder {

        val binding = ItemLocationSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: LocationViewHolder,
        position: Int
    ) {

        val location = locations[position]

        holder.binding.apply {

            tvLocationName.text = location.name ?: "Unknown"

            tvLocationDetails.text = listOfNotNull(
                location.state,
                location.country
            ).joinToString(", ")

            root.setOnClickListener {
                onLocationClick(location)
            }
        }
    }

    override fun getItemCount(): Int = locations.size

    fun updateLocations(newLocations: List<LocationResponse>) {

        locations.clear()
        locations.addAll(newLocations)

        notifyDataSetChanged()
    }
}
