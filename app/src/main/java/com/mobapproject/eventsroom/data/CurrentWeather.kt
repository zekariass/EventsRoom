package com.mobapproject.eventsroom.data

data class CurrentWeather(
    val coord: Coordiantes,
    val weather: Array<Weather>,
    val base: String,
    val main: Main,
    val visibility: String,
    val wind: Wind,
    val clouds: Clouds,
    val dt: String,
    var sys: Sys,
    val timezone: String,
    val id: String,
    val name: String,
    val cod: String
)

data class Coordiantes(
    val lon: Double,
    val lat: Double
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Main(
    val temp: Double,
    val pressure: Double,
    val humidity: Double,
    val temp_min: Double,
    val temp_max: Double
)

data class Wind(
    val speed: Double,
    val deg: Double
)

data class Clouds(
    val all: Double
)

data class Sys(
    val type: Double,
    val id: Double,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)