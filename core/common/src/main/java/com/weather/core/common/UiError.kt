package com.weather.core.common

sealed class UiError {
    data object NetworkUnavailable : UiError()
    data object InvalidApiKey : UiError()
    data class HttpError(val code: Int, val message: String?) : UiError()
    data class ParsingError(val message: String?) : UiError()
    data class UnknownError(val message: String?) : UiError()
}
