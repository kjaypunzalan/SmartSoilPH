package com.iacademy.smartsoilph.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iacademy.smartsoilph.R
import java.text.SimpleDateFormat
import java.util.*

class WeatherForecastAdapter(private val forecasts: List<DailyWeather>) : RecyclerView.Adapter<WeatherForecastAdapter.ForecastViewHolder>() {

    class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(forecast: DailyWeather) {
            itemView.findViewById<TextView>(R.id.tv_date_rv).text = forecast.time // Adjust according to your date formatting
            itemView.findViewById<TextView>(R.id.tv_temp_rv).text = "${forecast.maxTemperature}"

            val dateString = forecast.time
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            val dayToday = date?.let { outputFormat.format(it) } ?: "Unknown Day"
            itemView.findViewById<TextView>(R.id.tv_day_rv).text = "$dayToday"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_weather_list, parent, false)
        return ForecastViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(forecasts[position])
    }

    override fun getItemCount() = forecasts.size
}
