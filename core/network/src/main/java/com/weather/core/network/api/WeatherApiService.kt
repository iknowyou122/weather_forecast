package com.weather.core.network.api

import com.weather.core.network.dto.ForecastResponse
import com.weather.core.network.dto.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "zh_tw",
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>

    @GET("forecast")
    suspend fun getFiveDayForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "zh_tw",
        @Query("appid") apiKey: String
    ): Response<ForecastResponse>
}