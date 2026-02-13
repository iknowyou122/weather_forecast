package com.weather.feature.forecast.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _selectedCityIdFlow = MutableStateFlow(getSelectedCityId())

    companion object {
        private const val PREFS_NAME = "city_prefs"
        private const val KEY_SELECTED_CITY_ID = "selected_city_id"

        // Static list of supported cities
        val SUPPORTED_CITIES = listOf(
            CityData("1668341", "Taipei", "TW", 25.0478, 121.5318, "Asia/Taipei"),
            CityData("1816670", "Tokyo", "JP", 35.6895, 139.6917, "Asia/Tokyo"),
            CityData("1850147", "Naha", "JP", 26.2124, 127.6792, "Asia/Tokyo"),
            CityData("2643743", "London", "GB", 51.5074, -0.1278, "Europe/London"),
            CityData("5128581", "New York", "US", 40.7128, -74.0060, "America/New_York"),
            CityData("5391959", "San Francisco", "US", 37.7749, -122.4194, "America/Los_Angeles"),
            CityData("5368361", "Los Angeles", "US", 34.0522, -118.2437, "America/Los_Angeles"),
            CityData("2950158", "Berlin", "DE", 52.5200, 13.4050, "Europe/Berlin"),
            CityData("2995469", "Marseille", "FR", 43.2965, 5.3698, "Europe/Paris"),
            CityData("2147714", "Sydney", "AU", -33.8688, 151.2093, "Australia/Sydney")
        )
    }

    fun getCities(): List<CityData> = SUPPORTED_CITIES

    fun getSelectedCityId(): String {
        return prefs.getString(KEY_SELECTED_CITY_ID, SUPPORTED_CITIES.first().id)
            ?: SUPPORTED_CITIES.first().id
    }

    fun selectCity(cityId: String) {
        prefs.edit { putString(KEY_SELECTED_CITY_ID, cityId) }
        _selectedCityIdFlow.value = cityId
    }

    fun getSelectedCityIdFlow(): Flow<String> = _selectedCityIdFlow.asStateFlow()

    fun searchCities(query: String): List<CityData> {
        if (query.isBlank()) return SUPPORTED_CITIES
        return SUPPORTED_CITIES.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.country.contains(query, ignoreCase = true)
        }
    }
}

data class CityData(
    val id: String,
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val timezone: String?
)
