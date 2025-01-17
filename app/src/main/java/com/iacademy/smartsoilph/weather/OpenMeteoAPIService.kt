    package com.iacademy.smartsoilph.weather

    import com.iacademy.smartsoilph.weather.WeatherResponse
    import retrofit2.Call
    import retrofit2.http.GET
    import retrofit2.http.Query

    interface OpenMeteoAPIService {
        @GET("v1/forecast")
        fun getWeatherForecast(
            @Query("latitude") latitude: Double,
            @Query("longitude") longitude: Double,
            @Query("current") current: String = "temperature_2m,relative_humidity_2m,precipitation,wind_speed_10m,weather_code",
            @Query("hourly") hourly: String = "wind_speed_80m",
            @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,precipitation_sum,weather_code",
            @Query("timezone") timezone: String = "Asia/Singapore",
        ): Call<WeatherResponse>
    }
