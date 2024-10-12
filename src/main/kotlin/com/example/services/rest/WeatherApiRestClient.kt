package com.example.services.rest

import com.example.model.dto.WeatherResponse
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.config.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

const val TIMEOUT = "rest.client.timeout"
const val BASE_URL = "rest.client.base_url"
const val API_KEY = "rest.client.api_key"
private const val MAX_RETRIES = "rest.client.retries"
const val QUERY_PARAMS = "rest.client.query_params"

class WeatherApiRestClient(private val config: ApplicationConfig,private val client: HttpClient = HttpClient {
    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = config.property(MAX_RETRIES).getString().toInt())
        exponentialDelay() }}){

    private val queryParams = config.configList(QUERY_PARAMS)

    suspend fun getWeatherByLocation(location: String): WeatherResponse {
        val response: HttpResponse = client.get(config.property(BASE_URL).getString()){

            // Delay of 500 milliseconds between API calls is implemented to comply with the API's rate limit,
            // which allows a maximum of 3 requests per second.
            delay(500)

            timeout {
                requestTimeoutMillis = config.property(TIMEOUT).getString().toLong()
            }

            parameter("location", location)
            parameter("apikey", config.property(API_KEY).getString())
            queryParams.forEach { paramConfig ->
                val name = paramConfig.property("name").getString()
                val value = paramConfig.property("value").getString()
                parameter(name, value)
            }
        }

        return Json.decodeFromString(response.bodyAsText())
    }
}