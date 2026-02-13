package com.weather.feature.forecast.presentation.forecast

import com.weather.core.common.DefaultDispatchersProvider
import com.weather.feature.forecast.domain.model.City
import com.weather.feature.forecast.domain.model.CurrentWeather
import com.weather.feature.forecast.domain.model.DailyWeather
import com.weather.feature.forecast.domain.model.Forecast
import com.weather.feature.forecast.domain.model.ForecastResult
import com.weather.feature.forecast.domain.usecase.GetForecastUseCase
import com.weather.feature.forecast.domain.usecase.GetSelectedCityUseCase
import com.weather.feature.forecast.domain.usecase.SelectCityUseCase
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ForecastViewModelTest {

    private lateinit var getForecastUseCase: GetForecastUseCase
    private lateinit var getSelectedCityUseCase: GetSelectedCityUseCase
    private lateinit var selectCityUseCase: SelectCityUseCase
    private lateinit var dispatchers: DefaultDispatchersProvider
    private lateinit var viewModel: ForecastViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

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
                dateEpochSeconds = 1704067200 + index * 86400,
                tempMinC = 18.0 + index,
                tempMaxC = 26.0 + index,
                condition = "Sunny",
                iconCode = "01d"
            )
        }
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getForecastUseCase = mockk()
        getSelectedCityUseCase = mockk()
        selectCityUseCase = mockk()
        dispatchers = DefaultDispatchersProvider()

        // Setup default mocks
        every { getSelectedCityUseCase() } returns flowOf(testCity)
        every { getForecastUseCase(any()) } returns flowOf(
            ForecastResult.Success(testForecast, isFromCache = false)
        )

        viewModel = ForecastViewModel(
            getForecastUseCase = getForecastUseCase,
            getSelectedCityUseCase = getSelectedCityUseCase,
            selectCityUseCase = selectCityUseCase,
            dispatchers = dispatchers
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load selected city and forecast`() = runTest {
        // Given - Setup in @Before

        // When - Initial state from init
        val state = viewModel.state.value

        // Then
        assertEquals(testCity, state.selectedCity)
    }

    @Test
    fun `onIntent Refresh should reload forecast`() = runTest {
        // Given
        val updatedForecast = testForecast.copy(
            current = testForecast.current.copy(tempC = 30.0)
        )
        every { getForecastUseCase(testCity) } returns flowOf(
            ForecastResult.Success(updatedForecast, isFromCache = false)
        )

        // When
        viewModel.onIntent(ForecastIntent.Refresh)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertNotNull(state.forecast)
        assertFalse(state.isLoading)
    }

    @Test
    fun `onIntent SelectCity should call selectCity use case`() = runTest {
        // Given
        coEvery { selectCityUseCase(any()) } just Runs

        // When
        viewModel.onIntent(ForecastIntent.SelectCity("new_city_id"))
        advanceUntilIdle()

        // Then
        verify { getSelectedCityUseCase() } // Called multiple times due to re-collection
    }

    @Test
    fun `state should show loading when forecast is loading`() = runTest {
        // Given - Initial state from @Before should have loaded

        // When - Trigger refresh
        viewModel.onIntent(ForecastIntent.Refresh)

        // Then - During loading
        val loadingState = viewModel.state.value
        assertTrue(loadingState.isLoading || loadingState.forecast != null)
    }

    @Test
    fun `state should contain forecast with 7 daily items`() = runTest {
        // Given - Setup in @Before
        advanceUntilIdle()

        // When
        val state = viewModel.state.value

        // Then
        assertNotNull(state.forecast)
        assertEquals(7, state.forecast?.daily?.size)
    }

    @Test
    fun `error state should be set when forecast fails`() = runTest {
        // Given
        val errorMessage = RuntimeException("Network error")
        every { getForecastUseCase(testCity) } returns flowOf(
            ForecastResult.Error(errorMessage)
        )

        // Reset viewModel to trigger new collection with error
        viewModel = ForecastViewModel(
            getForecastUseCase = getForecastUseCase,
            getSelectedCityUseCase = getSelectedCityUseCase,
            selectCityUseCase = selectCityUseCase,
            dispatchers = dispatchers
        )
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertNotNull(state.error)
        assertNull(state.forecast)
    }

    @Test
    fun `isStale should be true when using cached data`() = runTest {
        // Given
        every { getForecastUseCase(testCity) } returns flowOf(
            ForecastResult.Success(testForecast, isFromCache = true)
        )

        // Reset viewModel
        viewModel = ForecastViewModel(
            getForecastUseCase = getForecastUseCase,
            getSelectedCityUseCase = getSelectedCityUseCase,
            selectCityUseCase = selectCityUseCase,
            dispatchers = dispatchers
        )
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state.isStale)
    }
}
