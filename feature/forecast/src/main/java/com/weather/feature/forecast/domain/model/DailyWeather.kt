package com.weather.feature.forecast.domain.model

data class DailyWeather(
    val dateEpochSeconds: Long,
    val tempMinC: Double,
    val tempMaxC: Double,
    val condition: String,
    val iconCode: String? = null
)
