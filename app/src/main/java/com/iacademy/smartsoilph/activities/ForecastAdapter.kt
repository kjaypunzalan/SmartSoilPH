package com.iacademy.smartsoilph.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.datamodels.DailyForecast
import java.text.SimpleDateFormat
import java.util.*

class ForecastAdapter(private val forecasts: List<DailyForecast>) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_weather_list, parent, false)
        return ForecastViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val forecast = forecasts[position]
        holder.bind(forecast)
    }

    override fun getItemCount() = forecasts.size

    class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date_rv)
        private val tvMaxTemp: TextView = itemView.findViewById(R.id.tv_temp_rv)

        fun bind(forecast: DailyForecast) {
            tvDate.text = formatDate(forecast.time)
            tvMaxTemp.text = "Max Temp: ${forecast.maxTemperature}°C"
        }

        private fun formatDate(dateString: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
            val date = inputFormat.parse(dateString) ?: return dateString
            return outputFormat.format(date)
        }
    }
}
