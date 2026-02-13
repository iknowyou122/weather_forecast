package com.weather.core.database.di

import android.content.Context
import com.weather.core.database.WeatherDatabase
import com.weather.core.database.dao.ForecastDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWeatherDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return WeatherDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideForecastDao(database: WeatherDatabase): ForecastDao {
        return database.forecastDao()
    }
}
