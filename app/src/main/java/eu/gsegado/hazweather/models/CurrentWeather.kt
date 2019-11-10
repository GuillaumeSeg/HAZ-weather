package eu.gsegado.hazweather.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrentWeather(
    @field:Json(name = "temperature") val temperature: Double,
    @field:Json(name = "icon")        val displayIcon: String,
    @field:Json(name = "dewPoint")    val dewPoint: Double
)