package com.weather.feature.forecast.data.repository

import com.weather.core.common.DispatchersProvider
import com.weather.core.database.dao.ForecastDao
import com.weather.core.database.dao.ForecastWithDailyWeather
import com.weather.core.network.api.WeatherApiService
import com.weather.feature.forecast.data.mapper.mapToDomain
import com.weather.feature.forecast.data.mapper.toCacheEntity
import com.weather.feature.forecast.data.mapper.toDailyCacheEntities
import com.weather.feature.forecast.data.mapper.toDomain
import com.weather.feature.forecast.domain.model.City
import com.weather.feature.forecast.domain.model.Forecast
import com.weather.feature.forecast.domain.model.ForecastResult
import com.weather.feature.forecast.domain.repository.WeatherRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val forecastDao: ForecastDao,
    private val dispatchers: DispatchersProvider,
    @Named("weather_api_key") private val apiKey: String
) : WeatherRepository {

    override fun getForecast(city: City): Flow<ForecastResult> = flow {
        // Try to emit cached data first
        val cachedForecast = forecastDao.getForecastWithDailyWeather(city.id)
        if (cachedForecast != null) {
            emit(ForecastResult.Success(cachedForecast.toDomain(), isFromCache = true))
        }

        // Then fetch from remote
        try {
            val forecast = fetchFromRemote(city)
            // Save to cache
            forecastDao.insertForecastWithDaily(
                forecast.toCacheEntity(),
                forecast.toDailyCacheEntities()
            )
            emit(ForecastResult.Success(forecast, isFromCache = false))
        } catch (e: IOException) {
            handleError(e, cachedForecast)
        } catch (e: Exception) {
            handleError(e, cachedForecast)
        }
    }.flowOn(dispatchers.io)

    override suspend fun refreshForecast(city: City) {
        try {
            val forecast = fetchFromRemote(city)
            forecastDao.insertForecastWithDaily(
                forecast.toCacheEntity(),
                forecast.toDailyCacheEntities()
            )
        } catch (e: Exception) {
            // Silent fail on refresh
            throw e
        }
    }

    private suspend fun fetchFromRemote(city: City): Forecast {
        val trimmedKey = apiKey.trim()

        if (trimmedKey.isEmpty()) {
            throw IOException("HTTP_ERROR:401:Unauthorized:API_KEY_MISSING")
        }

                val currentResponse = try {

                    apiService.getCurrentWeather(city.lat, city.lon, apiKey = trimmedKey)

                } catch (e: Exception) {

                    throw IOException("CurrentWeather API call failed: ${e.message}", e)

                }

                

                if (!currentResponse.isSuccessful) {

                    val code = currentResponse.code()

                    val message = currentResponse.message()

                    val errorBody = currentResponse.errorBody()?.string()

                    throw IOException("HTTP_ERROR:$code:$message:$errorBody")

                }

                

                val forecastResponse = try {

                    apiService.getFiveDayForecast(city.lat, city.lon, apiKey = trimmedKey)

                } catch (e: Exception) {

                    throw IOException("FiveDayForecast API call failed: ${e.message}", e)

                }

        

                if (forecastResponse.isSuccessful) {

                    val currentBody = currentResponse.body()

                    val forecastBody = forecastResponse.body()

                    

                    if (currentBody != null && forecastBody != null) {

                        return mapToDomain(city, currentBody, forecastBody)

                    } else {

                        throw IOException("Empty response body: current=${currentBody == null}, forecast=${forecastBody == null}")

                    }

                } else {

                    val code = forecastResponse.code()

                    val message = forecastResponse.message()

                    val errorBody = forecastResponse.errorBody()?.string()

                    throw IOException("HTTP_ERROR:$code:$message:$errorBody")

                }

            }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<ForecastResult>.handleError(
        error: Throwable,
        cached: ForecastWithDailyWeather?
    ) {
        if (cached != null) {
            emit(ForecastResult.ErrorWithCache(cached.toDomain(), error))
        } else {
            emit(ForecastResult.Error(error))
        }
    }
}