package com.weather.feature.forecast.domain.repository

import com.weather.feature.forecast.domain.model.City
import kotlinx.coroutines.flow.Flow

interface CityRepository {
    fun getCities(): Flow<List<City>>
    fun getSelectedCity(): Flow<City?>
    suspend fun selectCity(cityId: String)
    suspend fun searchCities(query: String): List<City>
}
