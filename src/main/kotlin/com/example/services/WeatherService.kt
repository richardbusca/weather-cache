package com.example.services

import com.example.mappers.WeatherMapper
import com.example.model.dto.WeatherValues
import com.example.model.enums.GeographicLocation
import com.example.services.redis.RedisService
import com.example.services.rest.WeatherApiRestClient
import kotlinx.serialization.json.Json

class WeatherService(private val weatherApiRestClient: WeatherApiRestClient,
                     private val redisService: RedisService, private val weatherMapper: WeatherMapper = WeatherMapper()) {

    suspend fun getWeatherByLocation(location: GeographicLocation): WeatherValues? {
        try {
            return weatherMapper.mapToWeatherValues(weatherApiRestClient.getWeatherByLocation(location.getLocation()))
        } catch (e: RuntimeException){
            e.printStackTrace()
            return null
        }
    }

    fun getWeatherByLocationFromCache(location: GeographicLocation): WeatherValues? {
        try {
            val value = redisService.get(location.name)
            return value?.let { Json.decodeFromString<WeatherValues>(it) }
        } catch (e: RuntimeException){
            e.printStackTrace()
            return null
        }
    }
}