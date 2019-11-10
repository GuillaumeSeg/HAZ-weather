package eu.gsegado.hazweather.home

import android.util.Log
import androidx.lifecycle.ViewModel
import eu.gsegado.hazweather.repository.WeatherRepository

class HomeViewModel : ViewModel() {

    private val weatherRepository = WeatherRepository()

    fun requestWeather(lat: Double, lng: Double) {
        weatherRepository.fetch(lat,lng)
        Log.d("YOLO", "lat : "+ lat +"lng : "+ lng)
    }
}