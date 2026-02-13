package com.weather.feature.forecast.domain.repository

import com.weather.feature.forecast.domain.model.City
import com.weather.feature.forecast.domain.model.ForecastResult
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getForecast(city: City): Flow<ForecastResult>
    suspend fun refreshForecast(city: City)
}
