package com.weather.feature.forecast.domain.model

data class City(
    val id: String,
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val timezone: String? = null
)
