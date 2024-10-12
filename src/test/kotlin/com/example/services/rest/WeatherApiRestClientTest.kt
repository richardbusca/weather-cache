package com.example.services.rest

import com.example.model.dto.WeatherResponse
import com.example.utils.DummyFactory.WEATHER_RESPONSE
import com.example.utils.DummyFactory.readJsonFile
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.mockk.mockk
import org.junit.Test
import io.mockk.every
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import kotlin.test.assertFailsWith

class WeatherApiRestClientTest {
    private val mockConfig = mockk<ApplicationConfig>()

    @Test
    fun getWeatherByLocationShouldReturnWeatherResponseForValidLocation(): Unit = runBlocking{
        // arrange
        val mockClient = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    when (request.url.parameters["location"]) {
                        "valid-location" -> {
                            val dummyContent = readJsonFile(WEATHER_RESPONSE)
                            respond(
                                content = dummyContent,
                                status = HttpStatusCode.OK,
                                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                            )
                        }
                        else -> respondBadRequest()
                    }
                }
            }
        }
        every { mockConfig.property(BASE_URL).getString() } returns "http://example.com"
        every { mockConfig.property(TIMEOUT).getString() } returns "5000"
        every { mockConfig.property(API_KEY).getString() } returns "fake-api-key"
        every { mockConfig.configList(QUERY_PARAMS) } returns emptyList()
        val weatherClient = WeatherApiRestClient(mockConfig, mockClient, 0)

        // act
        val result : WeatherResponse = weatherClient.getWeatherByLocation("valid-location")

        // assertions
        assertEquals(25.0, result.data.timelines[0].intervals[0].values.temperature)
    }

    @Test
    fun getWeatherByLocationShouldThrowExceptionOnInternalError(): Unit = runBlocking {
        // arrange
        val mockClient = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    respondError(HttpStatusCode.InternalServerError)
                }
            }
        }
        every { mockConfig.property(BASE_URL).getString() } returns "http://example.com"
        every { mockConfig.property(TIMEOUT).getString() } returns "5000"
        every { mockConfig.property(API_KEY).getString() } returns "fake-api-key"
        every { mockConfig.configList(QUERY_PARAMS) } returns emptyList()

        // act
        val weatherClient = WeatherApiRestClient(mockConfig, mockClient, 0)

        // assertions
        assertFailsWith<RuntimeException> {
            weatherClient.getWeatherByLocation("valid-location")
        }
    }
}