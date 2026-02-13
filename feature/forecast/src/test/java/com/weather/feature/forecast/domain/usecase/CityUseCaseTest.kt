package com.weather.feature.forecast.domain.usecase

import com.weather.feature.forecast.domain.model.City
import com.weather.feature.forecast.domain.repository.CityRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCitiesUseCaseTest {

    private lateinit var cityRepository: CityRepository
    private lateinit var useCase: GetCitiesUseCase

    private val testCities = listOf(
        City("1", "Taipei", "TW", 25.0, 121.0, "Asia/Taipei"),
        City("2", "Tokyo", "JP", 35.0, 139.0, "Asia/Tokyo"),
        City("3", "New York", "US", 40.7, -74.0, "America/New_York")
    )

    @Before
    fun setup() {
        cityRepository = mockk()
        useCase = GetCitiesUseCase(cityRepository)
    }

    @Test
    fun `invoke should return list of cities from repository`() = runTest {
        // Given
        every { cityRepository.getCities() } returns flowOf(testCities)

        // When
        val result = useCase().toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(testCities, result[0])
    }

    @Test
    fun `invoke should return empty list when no cities available`() = runTest {
        // Given
        every { cityRepository.getCities() } returns flowOf(emptyList())

        // When
        val result = useCase().toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(emptyList<City>(), result[0])
    }
}

class GetSelectedCityUseCaseTest {

    private lateinit var cityRepository: CityRepository
    private lateinit var useCase: GetSelectedCityUseCase

    private val testCity = City("1", "Taipei", "TW", 25.0, 121.0, "Asia/Taipei")

    @Before
    fun setup() {
        cityRepository = mockk()
        useCase = GetSelectedCityUseCase(cityRepository)
    }

    @Test
    fun `invoke should return selected city from repository`() = runTest {
        // Given
        every { cityRepository.getSelectedCity() } returns flowOf(testCity)

        // When
        val result = useCase().toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(testCity, result[0])
    }

    @Test
    fun `invoke should return null when no city selected`() = runTest {
        // Given
        every { cityRepository.getSelectedCity() } returns flowOf(null)

        // When
        val result = useCase().toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(null, result[0])
    }
}

class SelectCityUseCaseTest {

    private lateinit var cityRepository: CityRepository
    private lateinit var useCase: SelectCityUseCase

    @Before
    fun setup() {
        cityRepository = mockk()
        useCase = SelectCityUseCase(cityRepository)
    }

    @Test
    fun `invoke should call repository selectCity`() = runTest {
        // Given
        coEvery { cityRepository.selectCity(any()) } returns Unit

        // When
        useCase("city123")

        // Then
        coVerify { cityRepository.selectCity("city123") }
    }
}
