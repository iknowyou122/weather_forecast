package com.weather.feature.forecast.domain.model

sealed class ForecastResult {
    data class Success(val forecast: Forecast, val isFromCache: Boolean) : ForecastResult()
    data class ErrorWithCache(val forecast: Forecast, val error: Throwable) : ForecastResult()
    data class Error(val error: Throwable) : ForecastResult()
}
