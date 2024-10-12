package com.example.tasks

import kotlinx.coroutines.runBlocking
import com.example.model.dto.WeatherValues
import com.example.model.enums.GeographicLocation
import com.example.services.WeatherService
import com.example.services.redis.RedisService
import io.ktor.server.config.*
import io.mockk.*
import org.junit.Test
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WeatherScheduledTaskTest {

    private lateinit var config: ApplicationConfig
    private lateinit var weatherService: WeatherService
    private lateinit var redisService: RedisService
    private lateinit var task: WeatherScheduledTask

    @Test
    fun executeShouldRefreshCacheForEachGeographicLocation() = runBlocking {
        // arrange
        config = mockk()
        weatherService = mockk()
        redisService = mockk()
        task = WeatherScheduledTask(config, weatherService, redisService)
        val mockWeatherValues : WeatherValues = mockk<WeatherValues>()
        val locations = GeographicLocation.entries

        every { config.property("tasks.weather.delay").getString() } returns "1000"
        every { config.property("tasks.weather.period").getString() } returns "5000"
        every { config.property("rest.client.retries").getString() } returns "3"
        coEvery { weatherService.getWeatherByLocation(any()) } returns mockWeatherValues

        // act
        task.execute()

        // assertions
        locations.forEach { location ->
            coVerify { weatherService.getWeatherByLocation(location) }
            verify { redisService.set(location.name, Json.encodeToString(mockWeatherValues)) }
        }
    }
}