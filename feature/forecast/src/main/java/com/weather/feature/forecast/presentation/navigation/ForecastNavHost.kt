package com.weather.feature.forecast.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.weather.feature.forecast.presentation.citylist.CityListScreen
import com.weather.feature.forecast.presentation.forecast.ForecastScreen

@Composable
fun ForecastNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ForecastRoutes.Forecast.route
    ) {
        composable(ForecastRoutes.Forecast.route) {
            ForecastScreen(
                onNavigateToCityList = {
                    navController.navigate(ForecastRoutes.CityList.route)
                }
            )
        }
        composable(ForecastRoutes.CityList.route) {
            CityListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
