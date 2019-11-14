package eu.gsegado.hazweather.home

import android.app.Application
import android.content.SharedPreferences
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.crashlytics.android.Crashlytics
import eu.gsegado.hazweather.Constants
import eu.gsegado.hazweather.models.CurrentWeather
import eu.gsegado.hazweather.models.DailyWeatherData
import eu.gsegado.hazweather.manager.SharedPreferencesManager
import eu.gsegado.hazweather.repository.WeatherRepository
import eu.gsegado.hazweather.tools.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private var latitude: Double? = null
    private var longitude: Double? = null

    val locationLabelLiveData = MutableLiveData<String>().apply { setValue("_") }
    val dateLabelLiveData = MutableLiveData<String>().apply { setValue("_") }
    val currentWeatherLabelLiveData = MutableLiveData<String>().apply { setValue("_") }
    val currentTemperature = MutableLiveData<Double>()
    val currentTemperatureMax = MutableLiveData<Double>()
    val currentTemperatureMin = MutableLiveData<Double>()
    val currentWeatherDisplayLiveData = MutableLiveData<Pair<Int, Int>>()
    val dewPoint = MutableLiveData<Double>()
    val tips = MutableLiveData<Int>()
    val moon1PhaseLiveData = MutableLiveData<Pair<Int, Int>>()
    val moon2PhaseLiveData = MutableLiveData<Pair<Int, Int>>()
    val dailyWeatherDataLiveData1 = MutableLiveData<DailyWeatherData>()
    val dailyWeatherDataLiveData2 = MutableLiveData<DailyWeatherData>()
    val dailyWeatherDataLiveData3 = MutableLiveData<DailyWeatherData>()
    val dailyWeatherDataLiveData4 = MutableLiveData<DailyWeatherData>()
    val unitLabelTempLiveData = MutableLiveData<Constants.UnitSystem>(Constants.UnitSystem.KELVIN)

    private val compositeDisposable = CompositeDisposable()

    private val weatherRepository = WeatherRepository()
    private val sharedPreferencesManager: SharedPreferencesManager by lazy {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
        SharedPreferencesManager(preferences)
    }

    init {
        computeDate()
        computeTips()

        sharedPreferencesManager.unitsBehaviorSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onNext = { unitPref ->
                unitLabelTempLiveData.postValue(unitPref)

                weatherRepository.weatherBehaviorSubject.value?.let {
                    computeCurrentWeather(it.current, unitPref)
                    if (it.daily.data.isNotEmpty()) {
                        computeMaxMinCurrent(it.daily.data[0], unitPref)
                        computeNextDays(it.daily.data)
                    }
                }
            }, onError = {
                Crashlytics.logException(it)
            })
            .addTo(compositeDisposable)

        weatherRepository.weatherBehaviorSubject
            .subscribeOn(Schedulers.io())
            .map { result ->
                val unitPref = sharedPreferencesManager.preferences.getString(SharedPreferencesManager.KEY_UNITS, "K")?.let {
                    Constants.UnitSystem.from(it)
                } ?: Constants.UnitSystem.KELVIN

                unitLabelTempLiveData.postValue(unitPref)
                computeCurrentWeather(result.current, unitPref)
                if (result.daily.data.isNotEmpty()) {
                    computeMaxMinCurrent(result.daily.data[0], unitPref)
                    computeMoonsPhases(result.daily.data[0].moonPhase)
                    computeNextDays(result.daily.data)
                }
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
        sharedPreferencesManager.clear()
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
        cal.add(Calendar.YEAR, 22)
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

    fun computeLocation(sector: String) {
        val sectorLabel = Utils.getRandomSectorLabel()
        locationLabelLiveData.value = "$sectorLabel $sector"
    }

    private fun computeCurrentWeather(current: CurrentWeather, unit: Constants.UnitSystem) {
        currentWeatherLabelLiveData.postValue(current.displayIcon)

        when (unit) {
            Constants.UnitSystem.KELVIN -> {
                currentTemperature.postValue(Utils.celsiusToKelvin(current.temperature))
                dewPoint.postValue(Utils.celsiusToKelvin(current.dewPoint))
            }
            Constants.UnitSystem.FAHRENHEIT -> {
                currentTemperature.postValue(Utils.celsiusToFahrenheit(current.temperature))
                dewPoint.postValue(Utils.celsiusToFahrenheit(current.dewPoint))
            }
        }

        currentWeatherDisplayLiveData.postValue(Utils.getWeatherType(current.displayIcon))
    }

    private fun computeMaxMinCurrent(dailyWeather: DailyWeatherData, unit: Constants.UnitSystem) {
        when (unit) {
            Constants.UnitSystem.KELVIN -> {
                currentTemperatureMax.postValue(Utils.celsiusToKelvin(dailyWeather.temperatureMax))
                currentTemperatureMin.postValue(Utils.celsiusToKelvin(dailyWeather.temperatureMin))
            }
            Constants.UnitSystem.FAHRENHEIT -> {
                currentTemperatureMax.postValue(Utils.celsiusToFahrenheit(dailyWeather.temperatureMax))
                currentTemperatureMin.postValue(Utils.celsiusToFahrenheit(dailyWeather.temperatureMin))
            }
        }
    }

    private fun computeTips() {
        val tipsIndex = (Constants.listOfTips.indices).random()
        val tipsId = Constants.listOfTips[tipsIndex]
        tips.value = tipsId
    }

    private fun computeMoonsPhases(moon1Phase: Float) {
        Utils.getPhase(moon1Phase)?.let {
            moon1PhaseLiveData.postValue(it)
        }
        Utils.getPhase((cos(5.0f*moon1Phase+20.0f)+1.0f)/2.0f)?.let {
            moon2PhaseLiveData.postValue(it)
        }
    }

    private fun computeNextDays(days: List<DailyWeatherData>) {
        if (days.isNotEmpty()) {
            if (days.size > 1) {
                dailyWeatherDataLiveData1.postValue(days[1])
            }
            if (days.size > 2) {
                dailyWeatherDataLiveData2.postValue(days[2])
            }
            if (days.size > 3) {
                dailyWeatherDataLiveData3.postValue(days[3])
            }
            if (days.size > 4) {
                dailyWeatherDataLiveData4.postValue(days[4])
            }
        }
    }
}