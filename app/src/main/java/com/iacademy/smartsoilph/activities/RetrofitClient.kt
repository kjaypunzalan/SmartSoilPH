package com.iacademy.smartsoilph.activities

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.weatherbit.io/v2.0/" // Base URL for Weatherbit API

    val instance: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}