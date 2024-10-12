package com.example.services

import com.example.mappers.WeatherMapper
import com.example.model.dto.WeatherValues
import com.example.model.enums.GeographicLocation
import com.example.services.redis.RedisService
import com.example.services.rest.WeatherApiRestClient
import kotlinx.serialization.json.Json

class WeatherService(private val weatherApiRestClient: WeatherApiRestClient,
                     private val redisService: RedisService) {
    private val weatherMapper = WeatherMapper()

    suspend fun getWeatherByLocation(location: GeographicLocation): WeatherValues? {
        return weatherMapper.mapToWeatherValues(weatherApiRestClient.getWeatherByLocation(location.getLocation()))
    }

    fun getWeatherByLocationFromCache(location: GeographicLocation): WeatherValues? {
        return Json.decodeFromString(redisService.get(location.name))
    }
}