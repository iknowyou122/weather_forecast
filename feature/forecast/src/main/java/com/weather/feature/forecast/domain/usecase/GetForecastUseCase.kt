package com.weather.feature.forecast.domain.usecase

import com.weather.feature.forecast.domain.model.City
import com.weather.feature.forecast.domain.model.ForecastResult
import com.weather.feature.forecast.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    operator fun invoke(city: City): Flow<ForecastResult> {
        return weatherRepository.getForecast(city)
    }
}
