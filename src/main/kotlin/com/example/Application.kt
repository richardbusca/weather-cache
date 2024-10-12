package com.example

import com.example.plugins.*
import com.example.services.WeatherService
import com.example.services.redis.RedisService
import com.example.services.rest.WeatherApiRestClient
import com.example.tasks.TaskRegistry
import com.example.tasks.WeatherScheduledTask
import com.example.tasks.scheduleTask
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    installFeatures()
    val redisConfig = createRedisConfig(environment.config)
    val weatherService = createWeatherService(redisConfig, environment.config)
    configureRouting(weatherService)
    registerAllTasks(environment.config, weatherService, redisConfig)
    scheduleAllTasks()
}

@OptIn(ExperimentalSerializationApi::class)
private fun Application.installFeatures() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            namingStrategy = JsonNamingStrategy.SnakeCase
        })
    }
}

private fun Application.createWeatherService(redisService: RedisService, config: ApplicationConfig): WeatherService {
    return WeatherService(WeatherApiRestClient(environment.config), redisService, config)
}

private fun scheduleAllTasks() {
    TaskRegistry.getTasks().forEach { task ->
        scheduleTask(task, task.getDelay(), task.getPeriod())
    }
}

private fun registerAllTasks(
    config: ApplicationConfig,
    weatherService: WeatherService,
    redisService: RedisService
) {
    TaskRegistry.register(WeatherScheduledTask(config, weatherService, redisService))
}

private fun createRedisConfig(config: ApplicationConfig): RedisService {
    return RedisService(config).apply { configureRedis() }
}