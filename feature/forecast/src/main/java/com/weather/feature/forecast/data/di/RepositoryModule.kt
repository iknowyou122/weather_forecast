package com.weather.feature.forecast.data.di

import com.weather.feature.forecast.domain.repository.CityRepository
import com.weather.feature.forecast.domain.repository.WeatherRepository
import com.weather.feature.forecast.data.repository.CityRepositoryImpl
import com.weather.feature.forecast.data.repository.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        impl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindCityRepository(
        impl: CityRepositoryImpl
    ): CityRepository
}
