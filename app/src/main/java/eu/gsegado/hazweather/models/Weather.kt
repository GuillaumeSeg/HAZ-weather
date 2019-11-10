package eu.gsegado.hazweather.models

import com.squareup.moshi.Json

data class Weather(
    @field:Json(name = "latitude")  val id: Float? = null,
    @field:Json(name = "longitude") val name: Float? = null,
    @field:Json(name = "timezone")  val timezone: String ? = null
)