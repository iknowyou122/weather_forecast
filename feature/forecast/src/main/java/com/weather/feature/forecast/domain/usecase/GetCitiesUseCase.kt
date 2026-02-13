package com.weather.feature.forecast.domain.usecase

import com.weather.feature.forecast.domain.model.City
import com.weather.feature.forecast.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCitiesUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    operator fun invoke(): Flow<List<City>> {
        return cityRepository.getCities()
    }
}
