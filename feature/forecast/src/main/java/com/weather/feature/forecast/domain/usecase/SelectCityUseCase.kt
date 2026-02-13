package com.weather.feature.forecast.domain.usecase

import com.weather.feature.forecast.domain.repository.CityRepository
import javax.inject.Inject

class SelectCityUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(cityId: String) {
        cityRepository.selectCity(cityId)
    }
}
