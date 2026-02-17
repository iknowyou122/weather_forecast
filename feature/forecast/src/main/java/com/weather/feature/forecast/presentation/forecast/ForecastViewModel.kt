package com.weather.feature.forecast.presentation.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.core.common.DispatchersProvider
import com.weather.core.common.UiError
import com.weather.feature.forecast.domain.model.City
import com.weather.feature.forecast.domain.model.ForecastResult
import com.weather.feature.forecast.domain.usecase.GetForecastUseCase
import com.weather.feature.forecast.domain.usecase.GetSelectedCityUseCase
import com.weather.feature.forecast.domain.usecase.SelectCityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val getForecastUseCase: GetForecastUseCase,
    private val getSelectedCityUseCase: GetSelectedCityUseCase,
    private val selectCityUseCase: SelectCityUseCase,
    private val dispatchers: DispatchersProvider
) : ViewModel() {

    private val _state = MutableStateFlow(ForecastUiState())
    val state: StateFlow<ForecastUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ForecastEffect>()
    val effect: SharedFlow<ForecastEffect> = _effect.asSharedFlow()

    private val _selectedCity = MutableStateFlow<City?>(null)

    init {
        // Observe selected city changes
        getSelectedCityUseCase()
            .onEach { city ->
                _selectedCity.value = city
                _state.update { it.copy(selectedCity = city) }
                city?.let { loadForecast(it) }
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: ForecastIntent) {
        when (intent) {
            is ForecastIntent.Init -> {
                // Handled by init block observing selected city
            }
            is ForecastIntent.Refresh -> {
                _selectedCity.value?.let { loadForecast(it, isRefresh = true) }
            }
            is ForecastIntent.SelectCity -> {
                viewModelScope.launch(dispatchers.io) {
                    selectCityUseCase(intent.cityId)
                }
            }
        }
    }

    private fun loadForecast(city: City, isRefresh: Boolean = false) {
        getForecastUseCase(city)
            .onEach { result ->
                when (result) {
                    is ForecastResult.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                forecast = result.forecast,
                                isStale = result.isFromCache,
                                error = null
                            )
                        }
                        if (result.isFromCache) {
                            _effect.emit(ForecastEffect.ShowToast("顯示快取資料"))
                        }
                    }
                    is ForecastResult.ErrorWithCache -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                forecast = result.forecast,
                                isStale = true,
                                error = mapError(result.error)
                            )
                        }
                        _effect.emit(ForecastEffect.ShowToast("更新失敗，顯示快取資料"))
                    }
                    is ForecastResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = mapError(result.error),
                                forecast = null
                            )
                        }
                    }
                }
            }
            .catch { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = mapError(error)
                    )
                }
            }
            .launchIn(viewModelScope)

        if (!isRefresh) {
            _state.update { it.copy(isLoading = true) }
        }
    }

    private fun mapError(error: Throwable): UiError {
        return when (error) {
            is IOException -> {
                val message = error.message ?: ""
                if (message.startsWith("HTTP_ERROR:")) {
                    val parts = message.split(":")
                    val code = parts.getOrNull(1)?.toIntOrNull() ?: 0
                    val msg = parts.getOrNull(2) ?: "Unknown API Error"
                    val details = parts.getOrNull(3) ?: ""
                    
                    if (code == 401) {
                        UiError.InvalidApiKey
                    } else {
                        UiError.HttpError(code, "$msg $details")
                    }
                } else {
                    UiError.NetworkUnavailable
                }
            }
            is HttpException -> UiError.HttpError(error.code(), error.message())
            else -> UiError.UnknownError(error.message)
        }
    }

    fun navigateToCityList() {
        viewModelScope.launch {
            _effect.emit(ForecastEffect.NavigateToCityList)
        }
    }
}
