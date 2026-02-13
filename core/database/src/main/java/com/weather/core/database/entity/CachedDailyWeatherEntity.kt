package com.weather.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cached_daily_weather",
    foreignKeys = [
        ForeignKey(
            entity = CachedForecastEntity::class,
            parentColumns = ["cityId"],
            childColumns = ["cityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cityId"])]
)
data class CachedDailyWeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cityId: String,
    val dateEpochSeconds: Long,
    val tempMinC: Double,
    val tempMaxC: Double,
    val condition: String,
    val iconCode: String?
)
