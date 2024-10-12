package com.example.taks

import com.example.model.enums.GeographicLocation
import com.example.services.WeatherService
import com.example.services.redis.RedisService
import io.ktor.server.config.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WeatherScheduledTask(private val config: ApplicationConfig,
                           private val weatherService: WeatherService,
                           private val redisService: RedisService) : ScheduledTask() {
    override fun execute() {
        CoroutineScope(Dispatchers.Default).launch {
            GeographicLocation.entries.forEach { location ->
                try {
                    val weatherResponse = weatherService.getWeatherByLocation(location)
                    redisService.set(location.name, Json.encodeToString(weatherResponse))
                } catch (e: RuntimeException){
                    println("Error fetching weather for ${location.getLocation()}: ${e.message}")
                }
            }
        }
    }

    override fun getDelay(): Long = config.property("tasks.weather.delay").getString().toLong()

    override fun getPeriod(): Long = config.property("tasks.weather.period").getString().toLong()
}