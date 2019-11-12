package eu.gsegado.hazweather.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import eu.gsegado.hazweather.Constants
import eu.gsegado.hazweather.R
import eu.gsegado.hazweather.models.DailyWeatherData
import eu.gsegado.hazweather.tools.Utils
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_daily.view.*
import kotlinx.android.synthetic.main.layout_moon_phase.view.*
import org.joda.time.DateTime
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Constants.PERMISSION_REQUEST_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted
                    getLastKnowLocation()
                } else {
                    // permission denied

                }
                return
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun bind() {
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
            current_weather_temperature.text = Utils.displayTemperature(this, currentTemperature, "#.#")
        })
        homeViewModel.currentTemperatureMax.observe(this, Observer<Double> { currentTemperatureMax ->
            val tmaxFormatted = Utils.fromHtml(String.format(getString(R.string.tmax), Utils.displayTemperature(this, currentTemperatureMax, "#.#")))
            current_weather_temperature_max.text = tmaxFormatted
        })
        homeViewModel.currentTemperatureMin.observe(this, Observer<Double> { currentTemperatureMin ->
            val tminFormatted = Utils.fromHtml(String.format(getString(R.string.tmin), Utils.displayTemperature(this, currentTemperatureMin, "#.#")))
            current_weather_temperature_min.text = tminFormatted
        })
        homeViewModel.dewPoint.observe(this, Observer<Double> { dewPoint ->
            current_weather_dew_point_value.text = Utils.displayTemperature(this, dewPoint, "#.#")
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
            day1.tmax.text = String.format(getString(R.string.tmax_daily), Utils.displayTemperature(this, Utils.celsiusToKelvin(weather.temperatureMax), "#"))
            day1.tmin.text = String.format(getString(R.string.tmin_daily), Utils.displayTemperature(this, Utils.celsiusToKelvin(weather.temperatureMin), "#"))
        })
        homeViewModel.dailyWeatherDataLiveData2.observe(this, Observer<DailyWeatherData> { weather ->
            val dateTime = DateTime(weather.day.toLong()*1000)
            day2.date.text = dateTime.dayOfWeek().asShortText.capitalize()
            day2.tmax.text = String.format(getString(R.string.tmax_daily), Utils.displayTemperature(this, Utils.celsiusToKelvin(weather.temperatureMax), "#"))
            day2.tmin.text = String.format(getString(R.string.tmin_daily), Utils.displayTemperature(this, Utils.celsiusToKelvin(weather.temperatureMin), "#"))
        })
        homeViewModel.dailyWeatherDataLiveData3.observe(this, Observer<DailyWeatherData> { weather ->
            val dateTime = DateTime(weather.day.toLong()*1000)
            day3.date.text = dateTime.dayOfWeek().asShortText.capitalize()
            day3.tmax.text = String.format(getString(R.string.tmax_daily), Utils.displayTemperature(this, Utils.celsiusToKelvin(weather.temperatureMax), "#"))
            day3.tmin.text = String.format(getString(R.string.tmin_daily), Utils.displayTemperature(this, Utils.celsiusToKelvin(weather.temperatureMin), "#"))
        })
        homeViewModel.dailyWeatherDataLiveData4.observe(this, Observer<DailyWeatherData> { weather ->
            val dateTime = DateTime(weather.day.toLong()*1000)
            day4.date.text = dateTime.dayOfWeek().asShortText.capitalize()
            day4.tmax.text = String.format(getString(R.string.tmax_daily), Utils.displayTemperature(this, Utils.celsiusToKelvin(weather.temperatureMax), "#"))
            day4.tmin.text = String.format(getString(R.string.tmin_daily), Utils.displayTemperature(this, Utils.celsiusToKelvin(weather.temperatureMin), "#"))
        })
    }

    private fun getLastKnowLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            location?.let {
                homeViewModel.requestWeather(location.latitude, location.longitude)

                val geocoder = Geocoder(this, Locale.getDefault())
                homeViewModel.computeLocation(geocoder, location.latitude, location.longitude)
            }
        }
    }
}