package eu.gsegado.hazweather.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DailyWeather(
    @field:Json(name = "data") val data: MutableList<DailyWeatherData>
)