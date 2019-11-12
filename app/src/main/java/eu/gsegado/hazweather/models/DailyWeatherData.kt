package eu.gsegado.hazweather.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DailyWeatherData(
    @field:Json(name = "time")            val day: Int,
    @field:Json(name = "temperatureHigh") val temperatureMax: Double,
    @field:Json(name = "temperatureLow")  val temperatureMin: Double,
    @field:Json(name = "moonPhase")       val moonPhase: Float
)