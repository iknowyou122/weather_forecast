package com.weather.feature.forecast.presentation.citylist

import com.weather.core.common.UiError
import com.weather.feature.forecast.domain.model.City

data class CityListUiState(
    val cities: List<City> = emptyList(),
    val selectedCityId: String? = null,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val error: UiError? = null
)

sealed class CityListIntent {
    data object Init : CityListIntent()
    data class Search(val query: String) : CityListIntent()
    data class SelectCity(val cityId: String) : CityListIntent()
}

sealed class CityListEffect {
    data class NavigateBack(val cityId: String) : CityListEffect()
}
