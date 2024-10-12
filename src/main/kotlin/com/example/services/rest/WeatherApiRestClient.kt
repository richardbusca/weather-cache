package com.example.services.rest

import com.example.model.dto.WeatherResponse
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlin.random.Random

const val TIMEOUT = "rest.client.timeout"
const val BASE_URL = "rest.client.base_url"
const val API_KEY = "rest.client.api_key"
const val MAX_RETRIES = "rest.client.retries"
const val QUERY_PARAMS = "rest.client.query_params"

private const val SIMULATION_ERROR_PROBABILITY = 20

/**
 * REST client for interacting with the Weather API.
 *
 * @property config The application configuration containing API details.
 * @property client The HTTP client used for making requests, with built-in retry functionality.
 */
class WeatherApiRestClient(private val config: ApplicationConfig,private val client: HttpClient = HttpClient {
    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = config.property(MAX_RETRIES).getString().toInt())
        exponentialDelay() }}){

    private val queryParams = config.configList(QUERY_PARAMS)

    /**
     * Retrieves the weather information for a specific location.
     *
     * @param location The location for which to retrieve the weather data.
     * @return A [WeatherResponse] object containing the weather information.
     * @throws Exception if there is an error during the API call.
     */
    suspend fun getWeatherByLocation(location: String): WeatherResponse {
        val response = client.get(config.property(BASE_URL).getString()){

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

    /**
     * Simulates an error based on a random value.
     * Throws a RuntimeException if a randomly generated value
     * is below a certain threshold.
     */
    private fun simulateError() {
        val randomValue = Random.nextInt(1, 101)
        val errorThreshold = SIMULATION_ERROR_PROBABILITY
        if (randomValue <= errorThreshold) {
            throw RuntimeException("Simulated error: Random value $randomValue triggered an exception.")
        }
    }
}