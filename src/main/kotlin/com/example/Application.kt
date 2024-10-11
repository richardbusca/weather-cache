package com.example

import com.example.plugins.*
import com.example.services.rest.WeatherApiRestClient
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

private const val TIMEOUT = "rest.client.timeout"
private const val BASE_URL = "rest.client.base_url"
private const val API_KEY = "rest.client.api_key"
private const val MAX_RETRIES = "rest.client.retries"

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    installFeatures()
    val weatherApiRestClient = createWeatherApiRestClient()
    configureRouting(weatherApiRestClient)
}

@OptIn(ExperimentalSerializationApi::class)
private fun Application.installFeatures() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                namingStrategy = JsonNamingStrategy.SnakeCase
            }
        )
    }
}

private fun Application.createWeatherApiRestClient(): WeatherApiRestClient {
    val config = environment.config
    return WeatherApiRestClient(
        config.property(BASE_URL).getString(),
        config.property(API_KEY).getString(),
        config.property(MAX_RETRIES).getString().toInt(),
        config.property(TIMEOUT).getString().toLong()
    )
}
