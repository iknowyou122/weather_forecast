package com.weather.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.weather.core.database.dao.ForecastDao
import com.weather.core.database.entity.CachedDailyWeatherEntity
import com.weather.core.database.entity.CachedForecastEntity

@Database(
    entities = [
        CachedForecastEntity::class,
        CachedDailyWeatherEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun forecastDao(): ForecastDao

    companion object {
        private const val DATABASE_NAME = "weather_database"

        @Volatile
        private var instance: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): WeatherDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                WeatherDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}
