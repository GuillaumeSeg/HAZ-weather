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
import eu.gsegado.hazweather.tools.Utils
import kotlinx.android.synthetic.main.activity_home.*
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
            current_weather_temperature.text = Utils.displayTemperature(this, currentTemperature)
        })
        homeViewModel.currentTemperatureMax.observe(this, Observer<Double> { currentTemperatureMax ->
            val tmaxFormatted = String.format(getString(R.string.tmax), Utils.displayTemperature(this, currentTemperatureMax))
            current_weather_temperature_max.text = tmaxFormatted
        })
        homeViewModel.currentTemperatureMin.observe(this, Observer<Double> { currentTemperatureMin ->
            val tminFormatted = String.format(getString(R.string.tmin), Utils.displayTemperature(this, currentTemperatureMin))
            current_weather_temperature_min.text = tminFormatted
        })
        homeViewModel.dewPoint.observe(this, Observer<Double> { dewPoint ->
            current_weather_dew_point_value.text = Utils.displayTemperature(this, dewPoint)
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