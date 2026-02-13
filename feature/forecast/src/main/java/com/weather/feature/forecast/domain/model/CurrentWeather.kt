package com.weather.feature.forecast.domain.model

data class CurrentWeather(
    val tempC: Double,
    val tempMinC: Double,
    val tempMaxC: Double,
    val condition: String,
    val humidityPct: Int? = null,
    val windSpeedMs: Double? = null,
    val iconCode: String? = null
)
