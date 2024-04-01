package com.iacademy.smartsoilph.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iacademy.smartsoilph.R
import java.text.SimpleDateFormat
import java.util.*

class WeatherForecastAdapter(private val forecasts: List<DailyWeatherResponse>) : RecyclerView.Adapter<WeatherForecastAdapter.ForecastViewHolder>() {

    class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //initialize layout
        private val tvDay: TextView = itemView.findViewById(R.id.tv_day_rv)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date_rv)
        private val tvMaxTemp: TextView = itemView.findViewById(R.id.tv_temp_rv)
        private val ivWeather: ImageView = itemView.findViewById(R.id.iv_weather_rv)

        //bind data
        fun bind(forecast: DailyWeatherResponse) {
            tvDay.text = formatDate(forecast.time)
            tvDate.text = interpretWeatherCode(forecast.weatherCode)
            tvMaxTemp.text = "${forecast.maxTemperature}"
            ivWeather.setImageResource(getWeatherIcon(forecast.weatherCode))
        }

        private fun getDayOfWeek(dateString: String): String {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = format.parse(dateString) ?: return ""
            val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            return dayFormat.format(date)
        }

        private fun formatDate(dateString: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
            val date = inputFormat.parse(dateString) ?: return dateString
            return outputFormat.format(date)
        }

        private fun interpretWeatherCode(weatherCode: Int): String {
            return when (weatherCode) {
                0 -> "Clear sky"
                1, 2, 3 -> "Partly cloudy"
                45, 48 -> "Fog"
                51, 53, 55 -> "Drizzle"
                56, 57 -> "Freezing Drizzle"
                61, 63, 65 -> "Rain"
                66, 67 -> "Freezing Rain"
                71, 73, 75 -> "Snow"
                77 -> "Snow Grains"
                80, 81, 82 -> "Rain Showers"
                85, 86 -> "Snow Showers"
                95, 96, 99 -> "Thunderstorm"
                else -> "Unknown"
            }
        }

        private fun getWeatherIcon(weatherCode: Int): Int {
            return when (weatherCode) {
                0 -> R.drawable.img_clearsky
                1, 2, 3 -> R.drawable.img_overcast
                45, 48 -> R.drawable.img_fog
                51, 53, 55 -> R.drawable.img_drizzle
                56, 57 -> R.drawable.img_icedrizzle
                61, 63, 65 -> R.drawable.img_rainy
                66, 67 -> R.drawable.img_freezerain
                80, 81, 82 -> R.drawable.img_rainshowers
                95, 96, 99 -> R.drawable.img_thunderstorm
                else -> R.drawable.img_weather
            }
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
