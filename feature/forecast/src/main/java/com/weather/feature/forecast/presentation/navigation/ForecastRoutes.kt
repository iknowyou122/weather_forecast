package com.weather.feature.forecast.presentation.navigation

sealed class ForecastRoutes(val route: String) {
    data object Forecast : ForecastRoutes("forecast")
    data object CityList : ForecastRoutes("city_list")
}
