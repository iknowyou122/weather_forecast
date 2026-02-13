package com.weather.feature.forecast.data.repository

import com.weather.core.common.DispatchersProvider
import com.weather.feature.forecast.data.local.CityLocalDataSource
import com.weather.feature.forecast.data.mapper.toDomain
import com.weather.feature.forecast.domain.model.City
import com.weather.feature.forecast.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityRepositoryImpl @Inject constructor(
    private val localDataSource: CityLocalDataSource,
    private val dispatchers: DispatchersProvider
) : CityRepository {

    override fun getCities(): Flow<List<City>> {
        return kotlinx.coroutines.flow.flow {
            emit(localDataSource.getCities().map { it.toDomain() })
        }.flowOn(dispatchers.io)
    }

    override fun getSelectedCity(): Flow<City?> {
        return localDataSource.getSelectedCityIdFlow()
            .map { cityId ->
                localDataSource.getCities().find { it.id == cityId }?.toDomain()
            }
            .flowOn(dispatchers.io)
    }

    override suspend fun selectCity(cityId: String) {
        localDataSource.selectCity(cityId)
    }

    override suspend fun searchCities(query: String): List<City> {
        return localDataSource.searchCities(query).map { it.toDomain() }
    }
}
