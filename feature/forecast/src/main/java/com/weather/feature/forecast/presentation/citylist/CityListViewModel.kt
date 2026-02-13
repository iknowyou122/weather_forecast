package com.weather.feature.forecast.presentation.citylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.core.common.DispatchersProvider
import com.weather.feature.forecast.domain.model.City
import com.weather.feature.forecast.domain.usecase.GetCitiesUseCase
import com.weather.feature.forecast.domain.usecase.GetSelectedCityUseCase
import com.weather.feature.forecast.domain.usecase.SelectCityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityListViewModel @Inject constructor(
    private val getCitiesUseCase: GetCitiesUseCase,
    private val getSelectedCityUseCase: GetSelectedCityUseCase,
    private val selectCityUseCase: SelectCityUseCase,
    private val dispatchers: DispatchersProvider
) : ViewModel() {

    private val _state = MutableStateFlow(CityListUiState())
    val state: StateFlow<CityListUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<CityListEffect>()
    val effect: SharedFlow<CityListEffect> = _effect.asSharedFlow()

    private var allCities: List<City> = emptyList()

    init {
        loadCities()

        getSelectedCityUseCase()
            .onEach { city ->
                _state.update { it.copy(selectedCityId = city?.id) }
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: CityListIntent) {
        when (intent) {
            is CityListIntent.Init -> {
                loadCities()
            }
            is CityListIntent.Search -> {
                _state.update { it.copy(searchQuery = intent.query) }
                filterCities(intent.query)
            }
            is CityListIntent.SelectCity -> {
                selectCity(intent.cityId)
            }
        }
    }

    private fun loadCities() {
        _state.update { it.copy(isLoading = true) }

        getCitiesUseCase()
            .onEach { cities ->
                allCities = cities
                _state.update {
                    it.copy(
                        cities = cities,
                        isLoading = false,
                        error = null
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun filterCities(query: String) {
        val filtered = if (query.isBlank()) {
            allCities
        } else {
            allCities.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.country.contains(query, ignoreCase = true)
            }
        }
        _state.update { it.copy(cities = filtered) }
    }

    private fun selectCity(cityId: String) {
        viewModelScope.launch(dispatchers.io) {
            selectCityUseCase(cityId)
            _effect.emit(CityListEffect.NavigateBack(cityId))
        }
    }
}
