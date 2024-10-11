package com.example.mappers

import com.example.model.dto.WeatherResponse
import com.example.model.dto.WeatherValues

class WeatherMapper {
    fun mapToWeatherValues(response: WeatherResponse): WeatherValues? {
        return response.data.timelines
            .firstOrNull()?.intervals
            ?.firstOrNull()?.values
    }
}