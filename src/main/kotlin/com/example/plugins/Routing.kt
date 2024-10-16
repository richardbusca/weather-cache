package com.example.plugins

import com.example.model.dto.WeatherValues
import com.example.model.enums.GeographicLocation
import com.example.services.WeatherService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(weatherService: WeatherService) {
    routing {
        weatherRoute(weatherService)
    }
}

private fun Route.weatherRoute(weatherService: WeatherService) {
    get("/weather/{location}") {
        val locationParam = call.parameters["location"]
        val location = validateLocation(call, locationParam) ?: return@get

        val weatherValues: WeatherValues = weatherService.getWeatherByLocationFromCache(location)
            ?: return@get call.respondText("Weather data not found", status = HttpStatusCode.NotFound)

        call.respond(HttpStatusCode.OK, weatherValues)
    }
}

private suspend fun validateLocation(call: ApplicationCall, locationParam: String?): GeographicLocation? {
    return try {
        GeographicLocation.valueOf(locationParam?.uppercase() ?: "")
    } catch (e: IllegalArgumentException) {
        call.respondText("Invalid location", status = HttpStatusCode.BadRequest)
        null
    }
}
