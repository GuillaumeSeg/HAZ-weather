package eu.gsegado.hazweather.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Weather(
    @field:Json(name = "latitude")  val latitude: Float,
    @field:Json(name = "longitude") val longitude: Float,
    @field:Json(name = "timezone")  val timezone: String
)