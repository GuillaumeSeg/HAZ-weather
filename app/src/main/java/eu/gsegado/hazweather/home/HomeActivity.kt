package eu.gsegado.hazweather.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import eu.gsegado.hazweather.repository.WeatherRepository

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val weatherRepository = WeatherRepository()
    }
}