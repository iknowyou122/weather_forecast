package com.weather.feature.forecast.domain.usecase

import com.weather.feature.forecast.domain.model.City
import com.weather.feature.forecast.domain.model.CurrentWeather
import com.weather.feature.forecast.domain.model.DailyWeather
import com.weather.feature.forecast.domain.model.Forecast
import com.weather.feature.forecast.domain.model.ForecastResult
import com.weather.feature.forecast.domain.repository.WeatherRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetForecastUseCaseTest {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var useCase: GetForecastUseCase

    private val testCity = City(
        id = "1668341",
        name = "Taipei",
        country = "TW",
        lat = 25.0478,
        lon = 121.5318,
        timezone = "Asia/Taipei"
    )

    private val testForecast = Forecast(
        city = testCity,
        updatedAtEpochSeconds = 1704067200,
        current = CurrentWeather(
            tempC = 25.0,
            tempMinC = 20.0,
            tempMaxC = 28.0,
            condition = "Clear sky",
            humidityPct = 65,
            windSpeedMs = 3.5,
            iconCode = "01d"
        ),
        daily = List(7) { index ->
            DailyWeather(
                dateEpochSeconds = 1704067200 + index * 86400L,
                tempMinC = 18.0 + index,
                tempMaxC = 26.0 + index,
                condition = "Sunny",
                iconCode = "01d"
            )
        }
    )

    @Before
    fun setup() {
        weatherRepository = mockk()
        useCase = GetForecastUseCase(weatherRepository)
    }

    @Test
    fun `invoke should return forecast from repository`() = runTest {
        // Given
        val expectedResult = ForecastResult.Success(testForecast, isFromCache = false)
        every { weatherRepository.getForecast(testCity) } returns flowOf(expectedResult)

        // When
        val result = useCase(testCity).toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(expectedResult, result[0])
        verify { weatherRepository.getForecast(testCity) }
    }

    @Test
    fun `invoke should propagate errors from repository`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        val expectedResult = ForecastResult.Error(exception)
        every { weatherRepository.getForecast(testCity) } returns flowOf(expectedResult)

        // When
        val result = useCase(testCity).toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(expectedResult, result[0])
    }

    @Test
    fun `invoke should handle cache then remote pattern`() = runTest {
        // Given
        val cacheResult = ForecastResult.Success(testForecast, isFromCache = true)
        val remoteResult = ForecastResult.Success(testForecast, isFromCache = false)

        every { weatherRepository.getForecast(testCity) } returns flowOf(
            cacheResult,
            remoteResult
        )

        // When
        val results = useCase(testCity).toList()

        // Then
        assertEquals(2, results.size)
        assertEquals(cacheResult, results[0])
        assertEquals(remoteResult, results[1])
    }
}
