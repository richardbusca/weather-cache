package com.example.services.rest

import com.example.model.dto.WeatherResponse
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json


private const val FIELDS = "temperature,precipitationIntensity,windSpeed"
private const val TIMESTEPS = "current"
private const val UNITS = "metric"

class WeatherApiRestClient(private val baseUrl: String, private val apiKey: String, private val retries: Int, private val timeout: Long) {

    private val client = HttpClient {
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = retries)
            exponentialDelay()
        }
    }

    suspend fun getWeatherByLocation(location: String): WeatherResponse {
        val response: HttpResponse = client.get(baseUrl){
            timeout {
                requestTimeoutMillis = timeout
            }
            parameter("location", location)
            parameter("fields", FIELDS)
            parameter("timesteps", TIMESTEPS)
            parameter("units", UNITS)
            parameter("apikey", apiKey)
        }
        return Json.decodeFromString(response.bodyAsText())
    }
}