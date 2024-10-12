package com.example.mappers

import com.example.model.dto.WeatherResponse
import com.example.model.dto.WeatherValues

/**
 * Mapper to convert a weather response into specific weather values.
 */
class WeatherMapper {

    /**
     * Maps a weather response to weather values.
     *
     * @param response The weather response containing the data to be mapped.
     * @return A [WeatherValues] object containing the weather values,
     *         or `null` if no data is available in the response.
     */
    fun mapToWeatherValues(response: WeatherResponse): WeatherValues? {
        return response.data.timelines
            .firstOrNull()?.intervals
            ?.firstOrNull()?.values
    }
}