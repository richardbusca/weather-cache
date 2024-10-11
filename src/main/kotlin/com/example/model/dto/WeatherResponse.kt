package com.example.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val data: WeatherData
)

@Serializable
data class WeatherData(
    val timelines: List<Timeline>
)

@Serializable
data class Timeline(
    val timestep: String,
    val endTime: String,
    val startTime: String,
    val intervals: List<WeatherInterval>
)

@Serializable
data class WeatherInterval(
    val startTime: String,
    val values: WeatherValues
)

@Serializable
data class WeatherValues(
    val temperature: Double,
    val windSpeed: Double,
    val precipitationIntensity: Double
)