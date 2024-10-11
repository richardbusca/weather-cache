package com.example.model.enums

enum class GeographicLocation(val latitude: Double, val longitude: Double) {
    USA(37.0902, -95.7129),
    UK(51.5074, -0.1278),
    CANADA(56.1304, -106.3468);

    fun getLocation(): String{
        return "$latitude,$longitude"
    }
}