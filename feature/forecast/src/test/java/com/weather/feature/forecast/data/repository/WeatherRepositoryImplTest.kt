package com.weather.feature.forecast.data.repository

import com.weather.core.common.DispatchersProvider
import com.weather.core.database.dao.ForecastDao
import com.weather.core.network.api.WeatherApiService
import com.weather.core.network.dto.ForecastResponse
import com.weather.core.network.dto.WeatherResponse
import com.weather.feature.forecast.domain.model.City
import com.weather.feature.forecast.domain.model.ForecastResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRepositoryImplTest {

    private lateinit var apiService: WeatherApiService
    private lateinit var forecastDao: ForecastDao
    private lateinit var dispatchers: DispatchersProvider
    private lateinit var repository: WeatherRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    private val testCity = City(
        id = "1", name = "Taipei", country = "TW",
        lat = 25.0, lon = 121.0
    )
    private val apiKey = "  test_key  "

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        apiService = mockk()
        forecastDao = mockk(relaxed = true)
        dispatchers = mockk()
        
        every { dispatchers.io } returns testDispatcher
        coEvery { forecastDao.getForecastWithDailyWeather(any()) } returns null
        
        repository = WeatherRepositoryImpl(
            apiService = apiService,
            forecastDao = forecastDao,
            dispatchers = dispatchers,
            apiKey = apiKey
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getForecast should trim api key and call service`() = runTest(testDispatcher) {
        // Given
        val mockWeather = mockk<WeatherResponse>(relaxed = true)
        val mockForecast = mockk<ForecastResponse>(relaxed = true)
        
        coEvery { 
            apiService.getCurrentWeather(any(), any(), apiKey = "test_key") 
        } returns Response.success(mockWeather)
        
        coEvery { 
            apiService.getFiveDayForecast(any(), any(), apiKey = "test_key") 
        } returns Response.success(mockForecast)

        // When
        val results = repository.getForecast(testCity).toList()

        // Then
        // Should contain Success (after loading from cache which is null)
        assertTrue(results.any { it is ForecastResult.Success })
    }

    @Test
    fun `getForecast should throw IOException when API returns 401`() = runTest(testDispatcher) {
        // Given
        coEvery { 
            apiService.getCurrentWeather(any(), any(), apiKey = "test_key") 
        } returns Response.error(401, "Invalid Key".toResponseBody())

        // When
        val results = repository.getForecast(testCity).toList()

        // Then
        val errorResult = results.last() as ForecastResult.Error
        assertTrue(errorResult.error is IOException)
        val message = errorResult.error.message ?: ""
        assertTrue("Contains 401", message.contains("401"))
        assertTrue("Contains error body", message.contains("Invalid Key"))
    }

    @Test
    fun `getForecast should throw IOException when API key is empty after trim`() = runTest(testDispatcher) {
        // Given
        val emptyRepo = WeatherRepositoryImpl(
            apiService = apiService,
            forecastDao = forecastDao,
            dispatchers = dispatchers,
            apiKey = "   "
        )

        // When
        val results = emptyRepo.getForecast(testCity).toList()

        // Then
        val errorResult = results.last() as ForecastResult.Error
        assertTrue(errorResult.error.message?.contains("API_KEY_MISSING") == true)
    }
}
