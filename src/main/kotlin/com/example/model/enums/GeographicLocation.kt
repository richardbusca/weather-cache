package com.example.model.enums

enum class GeographicLocation(private val latitude: Double, private val longitude: Double) {
    CL(-33.4489, -70.6693),
    CH(47.3769, 8.5417),
    NZ(-36.8485, 174.7633),
    AU(-33.8688, 151.2093),
    UK(51.5074, -0.1278),
    USA(32.1656, -82.9001);

    fun getLocation(): String{
        return "$latitude,$longitude"
    }
}