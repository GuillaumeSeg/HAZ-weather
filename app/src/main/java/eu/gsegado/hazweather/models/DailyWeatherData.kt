package eu.gsegado.hazweather.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DailyWeatherData(
    @field:Json(name = "time") val day: Int
)