package eu.gsegado.hazweather.home

import android.app.Application
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import eu.gsegado.hazweather.Constants
import eu.gsegado.hazweather.R
import eu.gsegado.hazweather.models.CurrentWeather
import eu.gsegado.hazweather.models.DailyWeatherData
import eu.gsegado.hazweather.repository.WeatherRepository
import eu.gsegado.hazweather.tools.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private var latitude: Double? = null
    private var longitude: Double? = null

    val locationLabelLiveData = MutableLiveData<String>().apply { setValue("_") }
    val dateLabelLiveData = MutableLiveData<String>().apply { setValue("_") }
    val currentWeatherLabelLiveData = MutableLiveData<String>().apply { setValue("_") }
    val currentTemperature = MutableLiveData<Double>()
    val currentTemperatureMax = MutableLiveData<Double>()
    val currentTemperatureMin = MutableLiveData<Double>()
    val dewPoint = MutableLiveData<Double>()
    val tips = MutableLiveData<Int>()

    private val compositeDisposable = CompositeDisposable()

    private val weatherRepository = WeatherRepository()

    init {
        computeDate()
        computeTips()

        weatherRepository.weatherBehaviorSubject
            .subscribeOn(Schedulers.io())
            .map { result ->
                computeCurrentWeather(result.current)
                computeMaxMinCurrent(result.daily.data[0])
                return@map result
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = {
                Crashlytics.logException(it)
            })
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        weatherRepository.clear()
    }

    fun requestWeather(lat: Double?, lng: Double?) {
        latitude = lat
        longitude = lng
        Log.d("GPS", "lat : "+ lat +"lng : "+ lng)

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
                if (thoroughfareNameSplit.size >= 2) {
                    locationLabelLiveData.value = sectorLabel+" "+ thoroughfareNameSplit[1]
                } else {
                    locationLabelLiveData.value = sectorLabel+" "+ thoroughfareNameSplit[0]
                }
            } else {
                locationLabelLiveData.value = "$sectorLabel $cityName"
            }
        }
    }

    private fun computeCurrentWeather(current: CurrentWeather) {
        currentWeatherLabelLiveData.postValue(current.displayIcon)

        currentTemperature.postValue(Utils.celsiusToKelvin(current.temperature))
        dewPoint.postValue(Utils.celsiusToKelvin(current.dewPoint))
    }

    private fun computeMaxMinCurrent(dailyWeather: DailyWeatherData) {
        currentTemperatureMax.postValue(Utils.celsiusToKelvin(dailyWeather.temperatureMax))
        currentTemperatureMin.postValue(Utils.celsiusToKelvin(dailyWeather.temperatureLow))
    }

    private fun computeTips() {
        val tipsIndex = (Constants.listOfTips.indices).random()
        val tipsId = Constants.listOfTips[tipsIndex]
        tips.value = tipsId
    }
}