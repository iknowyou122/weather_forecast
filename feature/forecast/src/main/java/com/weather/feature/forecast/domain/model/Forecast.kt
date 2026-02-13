package com.weather.feature.forecast.domain.model

data class Forecast(
    val city: City,
    val updatedAtEpochSeconds: Long,
    val current: CurrentWeather,
    val daily: List<DailyWeather> // size = 7
)
