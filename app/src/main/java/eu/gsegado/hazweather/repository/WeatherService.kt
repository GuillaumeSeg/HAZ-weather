package eu.gsegado.hazweather.repository

import eu.gsegado.hazweather.models.Weather
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {
    @GET("forecast/{key}/{latitude},{longitude}/")
    fun getWeather(@Path("key") key: String,
                   @Path("latitude") latitude: Double,
                   @Path("longitude") longitude: Double,
                   @Query("units") units: String,
                   @Query("exclude") exclude: String,
                   @Query("lang") language: String): Single<Weather>
}