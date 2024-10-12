package com.example.services

import com.example.mappers.WeatherMapper
import com.example.model.dto.WeatherValues
import com.example.model.enums.GeographicLocation
import com.example.services.redis.RedisService
import com.example.services.rest.MAX_RETRIES
import com.example.services.rest.WeatherApiRestClient
import io.ktor.server.config.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

/**
 * Service for retrieving weather information from the Weather API and caching it in Redis.
 *
 * @property weatherApiRestClient The REST client used to communicate with the Weather API.
 * @property redisService The service used to interact with Redis for caching weather data.
 * @property weatherMapper The mapper for converting API responses into usable weather values.
 * @property config The application configuration containing Service details.
 */
class WeatherService(private val weatherApiRestClient: WeatherApiRestClient,
                     private val redisService: RedisService,
                     private val config: ApplicationConfig,
                     private val weatherMapper: WeatherMapper = WeatherMapper()
                     ) {

    /**
     * Retrieves weather information for a specific geographic location.
     *
     * @param location The geographic location for which to retrieve the weather data.
     * @return A [WeatherValues] object containing the weather information,
     *         or `null` if an error occurs during the retrieval process.
     */
    suspend fun getWeatherByLocation(location: GeographicLocation): WeatherValues? {
        try {
            val response = retry(config.property(MAX_RETRIES).getString().toInt()) {
                weatherApiRestClient.getWeatherByLocation(location.getLocation())
            }
            return weatherMapper.mapToWeatherValues(response)
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
    private suspend fun <T> retry(times: Int, block: suspend () -> T): T {
        var lastException: Exception? = null
        repeat(times) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                lastException = e
                println("Attempt ${attempt + 1} failed: ${e.message}. Retrying...")
                delay(500)
            }
        }
        throw lastException ?: Exception("Retry limit exceeded")
    }

}