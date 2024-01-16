package com.iacademy.smartsoilph.activities

import com.iacademy.smartsoilph.datamodels.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherbitAPIService {
    @GET("forecast/daily")
    fun getDailyForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("key") apiKey: String
    ): Call<WeatherResponse>
}
