package com.weather.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_forecasts")
data class CachedForecastEntity(
    @PrimaryKey
    val cityId: String,
    val cityName: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val timezone: String?,
    val currentTempC: Double,
    val currentTempMinC: Double,
    val currentTempMaxC: Double,
    val currentCondition: String,
    val currentHumidityPct: Int?,
    val currentWindSpeedMs: Double?,
    val currentIconCode: String?,
    val updatedAtEpochSeconds: Long
)
