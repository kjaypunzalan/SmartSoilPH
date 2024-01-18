package com.iacademy.smartsoilph.activities

import com.iacademy.smartsoilph.datamodels.CurrentConditionsResponse
import com.iacademy.smartsoilph.datamodels.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AccuWeatherAPIService {
    // Existing method for daily forecasts
    @GET("forecasts/v1/daily/1day/{locationKey}")
    fun getDailyForecast(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apiKey: String,
        @Query("metric") metric: Boolean = true
    ): Call<WeatherResponse>

    // New method for current conditions
    @GET("currentconditions/v1/{locationKey}")
    fun getCurrentConditions(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apiKey: String
    ): Call<List<CurrentConditionsResponse>>
}
