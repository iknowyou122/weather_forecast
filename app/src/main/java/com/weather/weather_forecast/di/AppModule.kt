package com.weather.weather_forecast.di

import com.weather.weather_forecast.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("weather_api_key")
    fun provideApiKey(): String {
        return BuildConfig.WEATHER_API_KEY
    }

    @Provides
    @Singleton
    @Named("weather_base_url")
    fun provideBaseUrl(): String {
        return BuildConfig.WEATHER_API_BASE_URL
    }
}
