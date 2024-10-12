package com.example.utils

import java.nio.file.Files
import java.nio.file.Paths

object DummyFactory {

    const val WEATHER_RESPONSE = "src/test/resources/weather_response.json"
    const val WEATHER_VALUES = "src/test/resources/weather_values.json"

    fun readJsonFile(filePath: String): String {
        return String(Files.readAllBytes(Paths.get(filePath)))
    }
}