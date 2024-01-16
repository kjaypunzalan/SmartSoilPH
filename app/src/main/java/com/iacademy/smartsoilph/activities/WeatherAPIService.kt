package com.iacademy.smartsoilph.activities

import com.iacademy.smartsoilph.datamodels.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
interface WeatherAPIService {
    @GET("weather")
    fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Call<WeatherResponse>
}