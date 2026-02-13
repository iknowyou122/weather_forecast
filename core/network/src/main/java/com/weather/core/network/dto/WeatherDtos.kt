package com.weather.core.network.dto

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("dt") val dt: Long,
    @SerializedName("main") val main: MainDto,
    @SerializedName("weather") val weather: List<WeatherConditionDto>,
    @SerializedName("wind") val wind: WindDto?,
    @SerializedName("name") val cityName: String
)

data class ForecastResponse(
    @SerializedName("list") val list: List<ForecastItemDto>
)

data class ForecastItemDto(
    @SerializedName("dt") val dt: Long,
    @SerializedName("main") val main: MainDto,
    @SerializedName("weather") val weather: List<WeatherConditionDto>
)

data class MainDto(
    @SerializedName("temp") val temp: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    @SerializedName("humidity") val humidity: Int?
)

data class WindDto(
    @SerializedName("speed") val speed: Double?
)

data class WeatherConditionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)