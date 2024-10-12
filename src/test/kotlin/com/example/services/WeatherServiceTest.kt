package com.example.services

import com.example.mappers.WeatherMapper
import com.example.model.dto.WeatherResponse
import com.example.model.dto.WeatherValues
import com.example.model.enums.GeographicLocation
import com.example.services.redis.RedisService
import com.example.services.rest.WeatherApiRestClient
import com.example.utils.DummyFactory.WEATHER_RESPONSE
import com.example.utils.DummyFactory.WEATHER_VALUES
import com.example.utils.DummyFactory.readJsonFile
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test

import org.junit.jupiter.api.Assertions.*

class WeatherServiceTest {

    private lateinit var weatherApiRestClient: WeatherApiRestClient
    private lateinit var redisService: RedisService
    private lateinit var weatherService: WeatherService
    private lateinit var weatherMapper: WeatherMapper

    @Before
    fun setUp() {
        weatherApiRestClient = mockk()
        redisService = mockk()
        weatherMapper = mockk()
        weatherService = WeatherService(weatherApiRestClient, redisService)
    }

    @Test
    fun getWeatherByLocationShouldReturnMappedWeatherValuesWhenAPICallIsSuccessful() = runBlocking {
        // Arrange
        val location = GeographicLocation.USA
        val apiResponse: WeatherResponse = Json.decodeFromString(readJsonFile(WEATHER_RESPONSE))
        val expectedWeatherValues : WeatherValues = Json.decodeFromString(readJsonFile(WEATHER_VALUES))

        coEvery { weatherApiRestClient.getWeatherByLocation(location.getLocation()) } returns apiResponse
        every { weatherMapper.mapToWeatherValues(apiResponse) } returns expectedWeatherValues

        // Act
        val result = weatherService.getWeatherByLocation(location)

        // Assert
        assertEquals(expectedWeatherValues, result)
        coVerify { weatherApiRestClient.getWeatherByLocation(location.getLocation()) }
    }

    @Test
    fun getWeatherByLocationShouldReturnNullWhenAPICallFails() = runBlocking {
        // Arrange
        val location = GeographicLocation.USA

        coEvery { weatherApiRestClient.getWeatherByLocation(location.getLocation()) } throws RuntimeException("API error")

        // Act
        val result = weatherService.getWeatherByLocation(location)

        // Assert
        assertNull(result)
        coVerify { weatherApiRestClient.getWeatherByLocation(location.getLocation()) }
    }

    @Test
    fun getWeatherByLocationFromCacheShouldReturnWeatherValuesFromCache() {
        // Arrange
        val location = GeographicLocation.USA
        val cachedJson = readJsonFile(WEATHER_VALUES)
        val expectedWeatherValues: WeatherValues = Json.decodeFromString(cachedJson)

        every { redisService.get(location.name) } returns cachedJson

        // Act
        val result = weatherService.getWeatherByLocationFromCache(location)

        // Assert
        assertEquals(expectedWeatherValues, result)
        verify { redisService.get(location.name) }
    }

    @Test
    fun getWeatherByLocationFromCacheShouldReturnNullIfCacheIsEmpty() {
        // Arrange
        val location = GeographicLocation.USA
        every { redisService.get(location.name) } returns null

        // Act
        val result = weatherService.getWeatherByLocationFromCache(location)

        // Assert
        assertNull(result)
        verify { redisService.get(location.name) }
    }
}