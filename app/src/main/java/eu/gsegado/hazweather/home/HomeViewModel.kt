package eu.gsegado.hazweather.home

import android.app.Application
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import eu.gsegado.hazweather.Constants
import eu.gsegado.hazweather.repository.WeatherRepository
import eu.gsegado.hazweather.tools.Utils
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private var latitude: Double? = null
    private var longitude: Double? = null

    val locationLabelLiveData = MutableLiveData<String>().apply { setValue("-") }
    val dateLabelLiveData = MutableLiveData<String>().apply { setValue("-") }

    private val weatherRepository = WeatherRepository()

    init {
        computeDate()
    }

    fun requestWeather(lat: Double?, lng: Double?) {
        latitude = lat
        longitude = lng
        Log.d("YOLO", "lat : "+ lat +"lng : "+ lng)

        weatherRepository.fetch(lat,lng)
    }

    fun computeDate() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, 12)
        val dateFormat = SimpleDateFormat("E dd.MM.y HH:mm", Locale.getDefault())
        val date = dateFormat.format(cal.time)
        dateLabelLiveData.value = date
    }

    fun computeLocation(geocoder: Geocoder, lat: Double?, lng: Double?) {
        Utils.safeLet(lat, lng) { latitudeSafe, longitudeSafe ->
            val addresses = geocoder.getFromLocation(latitudeSafe, longitudeSafe, 1)
            val cityName = addresses[0].locality
            val thoroughfareName = addresses[0].thoroughfare

            val sectorLabel = Utils.getRandomSectorLabel()
            if (!thoroughfareName.isNullOrEmpty()) {
                val thoroughfareNameSplit = thoroughfareName.split(" ", ignoreCase = false, limit = 2)
                locationLabelLiveData.value = sectorLabel+" "+ thoroughfareNameSplit[1]
            } else {
                locationLabelLiveData.value = "$sectorLabel $cityName"
            }
        }
    }
}