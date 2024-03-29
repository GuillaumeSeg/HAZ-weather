package eu.gsegado.hazweather.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.norbsoft.typefacehelper.TypefaceHelper
import eu.gsegado.hazweather.Constants
import eu.gsegado.hazweather.GlobalApplication
import eu.gsegado.hazweather.R
import eu.gsegado.hazweather.manager.SharedPreferencesManager
import eu.gsegado.hazweather.models.DailyWeatherData
import eu.gsegado.hazweather.settings.SettingsActivity
import eu.gsegado.hazweather.tools.Utils
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_daily.view.*
import kotlinx.android.synthetic.main.layout_moon_phase.view.*
import org.joda.time.DateTime
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var unitLabel = Constants.UnitSystem.KELVIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        moon_1.line_separator_1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorText))
        TypefaceHelper.typeface(this)

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        bind()

        // Check Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Constants.PERMISSION_REQUEST_FINE_LOCATION)
        } else {
            // Permission has already been granted
            getLastKnowLocation()
        }

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(GlobalApplication.localeManager?.setLocale(base))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Constants.PERMISSION_REQUEST_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted
                    getLastKnowLocation()
                } else {
                    // permission denied
                    val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                    preferences.getString(SharedPreferencesManager.KEY_LOCATION, "Paris")?.let {
                        if (it.isNotEmpty()) {
                            val geocoder = Geocoder(this, Locale.getDefault())
                            val places = geocoder.getFromLocationName(it, 1)
                            if (!places.isNullOrEmpty()) {
                                homeViewModel.computeLocation(places[0].locality, this)
                                homeViewModel.requestWeather(places[0].latitude, places[0].longitude)
                            }
                        }
                    }
                }
                return
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun bind() {
        settings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        homeViewModel.unitLabelTempLiveData.observe(this, Observer<Constants.UnitSystem> { unit ->
            unitLabel = unit
        })

        homeViewModel.locationLabelLiveData.observe(this, Observer<String> { locationLabel ->
            location_label.text = locationLabel
        })
        homeViewModel.dateLabelLiveData.observe(this, Observer<String> { dateLabel ->
            date_label.text = dateLabel
        })
        homeViewModel.currentWeatherLabelLiveData.observe(this, Observer<String> { currentWeatherLabel ->
            current_weather_label.text = currentWeatherLabel
        })
        homeViewModel.currentTemperature.observe(this, Observer<Double> { currentTemperature ->
            current_weather_temperature.text = Utils.displayTemperature(this, currentTemperature, "#.#", unitLabel)
        })
        homeViewModel.currentTemperatureMax.observe(this, Observer<Double> { currentTemperatureMax ->
            val tmaxFormatted = Utils.fromHtml(String.format(getString(R.string.tmax), Utils.displayTemperature(this, currentTemperatureMax, "#.#", unitLabel)))
            current_weather_temperature_max.text = tmaxFormatted
        })
        homeViewModel.currentTemperatureMin.observe(this, Observer<Double> { currentTemperatureMin ->
            val tminFormatted = Utils.fromHtml(String.format(getString(R.string.tmin), Utils.displayTemperature(this, currentTemperatureMin, "#.#", unitLabel)))
            current_weather_temperature_min.text = tminFormatted
        })
        homeViewModel.currentWeatherDisplayLiveData.observe(this, Observer<Pair<Int, Int>> { weatherDisplayIds ->
            current_weather_img.setImageResource(weatherDisplayIds.first)
            current_weather_label.text = getString(weatherDisplayIds.second).toUpperCase()
        })
        homeViewModel.dewPoint.observe(this, Observer<Double> { dewPoint ->
            current_weather_dew_point_value.text = Utils.displayTemperature(this, dewPoint, "#.#", unitLabel)
        })
        homeViewModel.tips.observe(this, Observer<Int> { tipsId ->
            tips.text = getString(tipsId)
        })

        moon_1.moon_name.text = getString(R.string.name_moon_alpha)
        moon_2.moon_name.text = getString(R.string.name_moon_zeta)

        homeViewModel.moon1PhaseLiveData.observe(this, Observer<Pair<Int, Int>> { moonPhaseIds ->
            moon_1.moon_phase.text = getString(moonPhaseIds.first)
            moon_1.moon_icon.setImageResource(moonPhaseIds.second)
        })
        homeViewModel.moon2PhaseLiveData.observe(this, Observer<Pair<Int, Int>> { moonPhaseIds ->
            moon_2.moon_phase.text = getString(moonPhaseIds.first)
            moon_2.moon_icon.setImageResource(moonPhaseIds.second)
        })

        homeViewModel.dailyWeatherDataLiveData1.observe(this, Observer<DailyWeatherData> { weather ->
            val dateTime = DateTime(weather.day.toLong()*1000)
            day1.date.text = dateTime.dayOfWeek().asShortText.capitalize()
            when (unitLabel) {
                Constants.UnitSystem.KELVIN -> {
                    day1.tmax.text = String.format(getString(R.string.tmax_daily), Utils.displayTemperature(this, Utils.celsiusToKelvin(weather.temperatureMax), "#", unitLabel))
                    day1.tmin.text = String.format(getString(R.string.tmin_daily), Utils.displayTemperature(this, Utils.celsiusToKelvin(weather.temperatureMin), "#", unitLabel))
                }
                Constants.UnitSystem.FAHRENHEIT -> {
                    day1.tmax.text = String.format(getString(R.string.tmax_daily), Utils.displayTemperature(this, Utils.celsiusToFahrenheit(weather.temperatureMax), "#", unitLabel))
                    day1.tmin.text = String.format(getString(R.string.tmin_daily), Utils.displayTemperature(this, Utils.celsiusToFahrenheit(weather.temperatureMin), "#", unitLabel))
                }
            }

            val weatherDisplay = Utils.getWeatherType(weather.displayIcon)
            day1.icon.setImageResource(weatherDisplay.first)
            day1.label.text = getString(weatherDisplay.second)
        })
        homeViewModel.dailyWeatherDataLiveData2.observe(this, Observer<DailyWeatherData> { weather ->
            val dateTime = DateTime(weather.day.toLong()*1000)
            day2.date.text = dateTime.dayOfWeek().asShortText.capitalize()
            when (unitLabel) {
                Constants.UnitSystem.KELVIN -> {
                    day2.tmax.text = String.format(getString(R.string.tmax_daily), Utils.displayTemperature(this, Utils.celsiusToKelvin(weather.temperatureMax), "#", unitLabel))
                    day2.tmin.text = String.format(getString(R.string.tmin_daily), Utils.displayTemperature(this, Utils.celsiusToKelvin(weather.temperatureMin), "#", unitLabel))
                }
                Constants.UnitSystem.FAHRENHEIT -> {
                    day2.tmax.text = String.format(getString(R.string.tmax_daily), Utils.displayTemperature(this, Utils.celsiusToFahrenheit(weather.temperatureMax), "#", unitLabel))
                    day2.tmin.text = String.format(getString(R.string.tmin_daily), Utils.displayTemperature(this, Utils.celsiusToFahrenheit(weather.temperatureMin), "#", unitLabel))
                }
            }
            val weatherDisplay = Utils.getWeatherType(weather.displayIcon)
            day2.icon.setImageResource(weatherDisplay.first)
            day2.label.text = getString(weatherDisplay.second)
        })
        homeViewModel.dailyWeatherDataLiveData3.observe(this, Observer<DailyWeatherData> { weather ->
            val dateTime = DateTime(weather.day.toLong()*1000)
            day3.date.text = dateTime.dayOfWeek().asShortText.capitalize()
            when (unitLabel) {
                Constants.UnitSystem.KELVIN -> {
                    day3.tmax.text = String.format(getString(R.string.tmax_daily), Utils.displayTemperature(this, Utils.celsiusToKelvin(weather.temperatureMax), "#", unitLabel))
                    day3.tmin.text = String.format(getString(R.string.tmin_daily), Utils.displayTemperature(this, Utils.celsiusToKelvin(weather.temperatureMin), "#", unitLabel))
                }
                Constants.UnitSystem.FAHRENHEIT -> {
                    day3.tmax.text = String.format(getString(R.string.tmax_daily), Utils.displayTemperature(this, Utils.celsiusToFahrenheit(weather.temperatureMax), "#", unitLabel))
                    day3.tmin.text = String.format(getString(R.string.tmin_daily), Utils.displayTemperature(this, Utils.celsiusToFahrenheit(weather.temperatureMin), "#", unitLabel))
                }
            }
            val weatherDisplay = Utils.getWeatherType(weather.displayIcon)
            day3.icon.setImageResource(weatherDisplay.first)
            day3.label.text = getString(weatherDisplay.second)
        })
        homeViewModel.dailyWeatherDataLiveData4.observe(this, Observer<DailyWeatherData> { weather ->
            val dateTime = DateTime(weather.day.toLong()*1000)
            day4.date.text = dateTime.dayOfWeek().asShortText.capitalize()
            when (unitLabel) {
                Constants.UnitSystem.KELVIN -> {
                    day4.tmax.text = String.format(getString(R.string.tmax_daily), Utils.displayTemperature(this, Utils.celsiusToKelvin(weather.temperatureMax), "#", unitLabel))
                    day4.tmin.text = String.format(getString(R.string.tmin_daily), Utils.displayTemperature(this, Utils.celsiusToKelvin(weather.temperatureMin), "#", unitLabel))
                }
                Constants.UnitSystem.FAHRENHEIT -> {
                    day4.tmax.text = String.format(getString(R.string.tmax_daily), Utils.displayTemperature(this, Utils.celsiusToFahrenheit(weather.temperatureMax), "#", unitLabel))
                    day4.tmin.text = String.format(getString(R.string.tmin_daily), Utils.displayTemperature(this, Utils.celsiusToFahrenheit(weather.temperatureMin), "#", unitLabel))
                }
            }
            val weatherDisplay = Utils.getWeatherType(weather.displayIcon)
            day4.icon.setImageResource(weatherDisplay.first)
            day4.label.text = getString(weatherDisplay.second)
        })
    }

    private fun getLastKnowLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            location?.let {
                homeViewModel.requestWeather(location.latitude, location.longitude)

                val geocoder = Geocoder(this, Locale.getDefault())
                homeViewModel.computeLocation(geocoder, location.latitude, location.longitude, this)
            } ?: run {
                Toast.makeText(this, getString(R.string.gps_error), Toast.LENGTH_SHORT).show()
            }
        }
    }
}