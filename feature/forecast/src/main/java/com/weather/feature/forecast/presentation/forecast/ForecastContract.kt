package com.weather.feature.forecast.presentation.forecast

import com.weather.core.common.UiError
import com.weather.feature.forecast.domain.model.City
import com.weather.feature.forecast.domain.model.Forecast

data class ForecastUiState(
    val selectedCity: City? = null,
    val isLoading: Boolean = false,
    val forecast: Forecast? = null,
    val error: UiError? = null,
    val isStale: Boolean = false
)

sealed class ForecastIntent {
    data object Init : ForecastIntent()
    data object Refresh : ForecastIntent()
    data class SelectCity(val cityId: String) : ForecastIntent()
}

sealed class ForecastEffect {
    data class ShowToast(val message: String) : ForecastEffect()
    data object NavigateToCityList : ForecastEffect()
}

// UI models for display
fun Forecast.toUiModel(): ForecastUi = ForecastUi(
    cityName = city.name,
    country = city.country,
    updatedAt = formatUpdatedTime(updatedAtEpochSeconds),
    current = CurrentWeatherUi(
        tempC = current.tempC.toInt(),
        tempMinC = current.tempMinC.toInt(),
        tempMaxC = current.tempMaxC.toInt(),
        condition = current.condition,
        humidityPct = current.humidityPct,
        windSpeedMs = current.windSpeedMs,
        iconCode = current.iconCode
    ),
    daily = daily.map { it.toUiModel() }
)

fun com.weather.feature.forecast.domain.model.DailyWeather.toUiModel(): DailyWeatherUi = DailyWeatherUi(
    dayName = formatDayName(dateEpochSeconds),
    date = formatDate(dateEpochSeconds),
    tempMinC = tempMinC.toInt(),
    tempMaxC = tempMaxC.toInt(),
    condition = condition,
    iconCode = iconCode
)

data class ForecastUi(
    val cityName: String,
    val country: String,
    val updatedAt: String,
    val current: CurrentWeatherUi,
    val daily: List<DailyWeatherUi>
)

data class CurrentWeatherUi(
    val tempC: Int,
    val tempMinC: Int,
    val tempMaxC: Int,
    val condition: String,
    val humidityPct: Int?,
    val windSpeedMs: Double?,
    val iconCode: String?
)

data class DailyWeatherUi(
    val dayName: String,
    val date: String,
    val tempMinC: Int,
    val tempMaxC: Int,
    val condition: String,
    val iconCode: String?
)

private fun formatUpdatedTime(epochSeconds: Long): String {
    val formatter = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    return formatter.format(java.util.Date(epochSeconds * 1000))
}

private fun formatDayName(epochSeconds: Long): String {
    val formatter = java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault())
    return formatter.format(java.util.Date(epochSeconds * 1000))
}

private fun formatDate(epochSeconds: Long): String {
    val formatter = java.text.SimpleDateFormat("MM/dd", java.util.Locale.getDefault())
    return formatter.format(java.util.Date(epochSeconds * 1000))
}
