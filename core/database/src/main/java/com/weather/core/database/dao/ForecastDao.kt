package com.weather.core.database.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.weather.core.database.entity.CachedDailyWeatherEntity
import com.weather.core.database.entity.CachedForecastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {

    @Query("SELECT * FROM cached_forecasts WHERE cityId = :cityId")
    fun getForecastByCityId(cityId: String): Flow<CachedForecastEntity?>

    @Query("SELECT * FROM cached_daily_weather WHERE cityId = :cityId ORDER BY dateEpochSeconds ASC")
    fun getDailyWeatherByCityId(cityId: String): Flow<List<CachedDailyWeatherEntity>>

    @Transaction
    @Query("SELECT * FROM cached_forecasts WHERE cityId = :cityId")
    suspend fun getForecastWithDailyWeather(cityId: String): ForecastWithDailyWeather?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: CachedForecastEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyWeather(dailyWeather: List<CachedDailyWeatherEntity>)

    @Transaction
    suspend fun insertForecastWithDaily(
        forecast: CachedForecastEntity,
        dailyWeather: List<CachedDailyWeatherEntity>
    ) {
        insertForecast(forecast)
        // Delete old daily weather for this city
        deleteDailyWeatherByCityId(forecast.cityId)
        insertDailyWeather(dailyWeather)
    }

    @Query("DELETE FROM cached_daily_weather WHERE cityId = :cityId")
    suspend fun deleteDailyWeatherByCityId(cityId: String)

    @Query("DELETE FROM cached_forecasts WHERE cityId = :cityId")
    suspend fun deleteForecastByCityId(cityId: String)
}

data class ForecastWithDailyWeather(
    @Embedded val forecast: CachedForecastEntity,
    @Relation(
        parentColumn = "cityId",
        entityColumn = "cityId"
    )
    val daily: List<CachedDailyWeatherEntity>
)
