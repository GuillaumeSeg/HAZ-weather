package eu.gsegado.hazweather.home

import android.util.Log
import androidx.lifecycle.ViewModel
import eu.gsegado.hazweather.repository.WeatherRepository

class HomeViewModel : ViewModel() {

    private var latitude: Double? = null
    private var longitude: Double? = null

    private val weatherRepository = WeatherRepository()

    fun requestWeather(lat: Double, lng: Double) {
        latitude = lat
        longitude = lng
        weatherRepository.fetch(lat,lng)
        Log.d("YOLO", "lat : "+ lat +"lng : "+ lng)
    }
}