package com.example.services

import com.example.mappers.WeatherMapper
import com.example.model.dto.WeatherValues
import com.example.model.enums.GeographicLocation
import com.example.services.redis.RedisService
import com.example.services.rest.WeatherApiRestClient
import kotlinx.serialization.json.Json

/**
 * Service for retrieving weather information from the Weather API and caching it in Redis.
 *
 * @property weatherApiRestClient The REST client used to communicate with the Weather API.
 * @property redisService The service used to interact with Redis for caching weather data.
 * @property weatherMapper The mapper for converting API responses into usable weather values.
 */
class WeatherService(private val weatherApiRestClient: WeatherApiRestClient,
                     private val redisService: RedisService, private val weatherMapper: WeatherMapper = WeatherMapper()) {

    /**
     * Retrieves weather information for a specific geographic location.
     *
     * @param location The geographic location for which to retrieve the weather data.
     * @return A [WeatherValues] object containing the weather information,
     *         or `null` if an error occurs during the retrieval process.
     */
    suspend fun getWeatherByLocation(location: GeographicLocation): WeatherValues? {
        try {
            return weatherMapper.mapToWeatherValues(weatherApiRestClient.getWeatherByLocation(location.getLocation()))
        } catch (e: RuntimeException){
            e.printStackTrace()
            return null
        }
    }

    /**
     * Retrieves cached weather information for a specific geographic location.
     *
     * @param location The geographic location for which to retrieve the cached weather data.
     * @return A [WeatherValues] object containing the cached weather information,
     *         or `null` if the data is not found or an error occurs during the retrieval process.
     */
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