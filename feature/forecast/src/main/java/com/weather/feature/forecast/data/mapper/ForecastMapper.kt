package com.weather.feature.forecast.data.mapper

import com.weather.core.database.dao.ForecastWithDailyWeather
import com.weather.core.database.entity.CachedDailyWeatherEntity
import com.weather.core.database.entity.CachedForecastEntity
import com.weather.core.network.dto.ForecastItemDto
import com.weather.core.network.dto.ForecastResponse
import com.weather.core.network.dto.WeatherResponse
import com.weather.feature.forecast.data.local.CityData
import com.weather.feature.forecast.domain.model.City
import com.weather.feature.forecast.domain.model.CurrentWeather
import com.weather.feature.forecast.domain.model.DailyWeather
import com.weather.feature.forecast.domain.model.Forecast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun mapToDomain(city: City, current: WeatherResponse, forecast: ForecastResponse): Forecast {
    return Forecast(
        city = city,
        updatedAtEpochSeconds = System.currentTimeMillis() / 1000,
        current = current.toDomain(),
        daily = forecast.toDailyDomain()
    )
}

fun WeatherResponse.toDomain(): CurrentWeather {
    val condition = weather.firstOrNull()?.description ?: "Unknown"
    val iconCode = weather.firstOrNull()?.icon
    return CurrentWeather(
        tempC = main.temp,
        tempMinC = main.tempMin,
        tempMaxC = main.tempMax,
        condition = condition,
        humidityPct = main.humidity,
        windSpeedMs = wind?.speed,
        iconCode = iconCode
    )
}

fun ForecastResponse.toDailyDomain(): List<DailyWeather> {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    // Group by date string
    return list.groupBy { 
        sdf.format(Date(it.dt * 1000)) 
    }.map { (dateStr, items) ->
        val firstItem = items.first()
        val minTemp = items.minOf { it.main.tempMin }
        val maxTemp = items.maxOf { it.main.tempMax }
        val condition = items[items.size / 2].weather.firstOrNull()?.description ?: "Unknown"
        val iconCode = items[items.size / 2].weather.firstOrNull()?.icon

        DailyWeather(
            dateEpochSeconds = firstItem.dt,
            tempMinC = minTemp,
            tempMaxC = maxTemp,
            condition = condition,
            iconCode = iconCode
        )
    }.take(7) // Free API gives 5 days, but we take up to 7 as per domain
}

// Cache mapping
fun Forecast.toCacheEntity(): CachedForecastEntity {
    return CachedForecastEntity(
        cityId = city.id,
        cityName = city.name,
        country = city.country,
        lat = city.lat,
        lon = city.lon,
        timezone = city.timezone,
        currentTempC = current.tempC,
        currentTempMinC = current.tempMinC,
        currentTempMaxC = current.tempMaxC,
        currentCondition = current.condition,
        currentHumidityPct = current.humidityPct,
        currentWindSpeedMs = current.windSpeedMs,
        currentIconCode = current.iconCode,
        updatedAtEpochSeconds = updatedAtEpochSeconds
    )
}

fun Forecast.toDailyCacheEntities(): List<CachedDailyWeatherEntity> {
    return daily.map { day ->
        CachedDailyWeatherEntity(
            cityId = city.id,
            dateEpochSeconds = day.dateEpochSeconds,
            tempMinC = day.tempMinC,
            tempMaxC = day.tempMaxC,
            condition = day.condition,
            iconCode = day.iconCode
        )
    }
}

fun ForecastWithDailyWeather.toDomain(): Forecast {
    return Forecast(
        city = City(
            id = forecast.cityId,
            name = forecast.cityName,
            country = forecast.country,
            lat = forecast.lat,
            lon = forecast.lon,
            timezone = forecast.timezone
        ),
        updatedAtEpochSeconds = forecast.updatedAtEpochSeconds,
        current = CurrentWeather(
            tempC = forecast.currentTempC,
            tempMinC = forecast.currentTempMinC,
            tempMaxC = forecast.currentTempMaxC,
            condition = forecast.currentCondition,
            humidityPct = forecast.currentHumidityPct,
            windSpeedMs = forecast.currentWindSpeedMs,
            iconCode = forecast.currentIconCode
        ),
        daily = daily.map { it.toDomain() }
    )
}

private fun CachedDailyWeatherEntity.toDomain(): DailyWeather {
    return DailyWeather(
        dateEpochSeconds = dateEpochSeconds,
        tempMinC = tempMinC,
        tempMaxC = tempMaxC,
        condition = condition,
        iconCode = iconCode
    )
}

// City mapping
fun CityData.toDomain(): City {
    return City(
        id = id,
        name = name,
        country = country,
        lat = lat,
        lon = lon,
        timezone = timezone
    )
}
