package com.example.services

import com.example.mappers.WeatherMapper
import com.example.model.dto.WeatherValues
import com.example.model.enums.GeographicLocation
import com.example.services.rest.WeatherApiRestClient

class WeatherService(private val weatherApiRestClient: WeatherApiRestClient) {
    private val weatherMapper = WeatherMapper()

    suspend fun getWeatherByLocation(location: GeographicLocation): WeatherValues? {
        return weatherMapper.mapToWeatherValues(weatherApiRestClient.getWeatherByLocation(location.getLocation()))
    }
}