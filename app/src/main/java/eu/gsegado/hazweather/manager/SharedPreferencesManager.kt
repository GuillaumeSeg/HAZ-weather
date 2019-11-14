package eu.gsegado.hazweather.manager

import android.content.SharedPreferences
import eu.gsegado.hazweather.Constants
import io.reactivex.subjects.BehaviorSubject

class SharedPreferencesManager(val preferences: SharedPreferences) {

    companion object {
        const val KEY_UNITS = "units"
        const val KEY_LOCATION = "location"
    }

    val unitsBehaviorSubject by lazy { BehaviorSubject.create<Constants.UnitSystem>() }
    val locationBehaviorSubject by lazy { BehaviorSubject.create<String>() }

    private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        key?.let {
            if (it == KEY_UNITS) {
                sharedPreferences?.getString(key, "K")?.let { unitChoice ->
                    when (unitChoice) {
                        Constants.UnitSystem.KELVIN.unit -> {
                            unitsBehaviorSubject.onNext(Constants.UnitSystem.KELVIN)
                        }
                        Constants.UnitSystem.FAHRENHEIT.unit -> {
                            unitsBehaviorSubject.onNext(Constants.UnitSystem.FAHRENHEIT)
                        }
                    }
                }
            }
            if (it == KEY_LOCATION) {
                sharedPreferences?.getString(key, "")?.let { locationChoice ->
                    locationBehaviorSubject.onNext(locationChoice)
                }
            }
        }
    }

    init {
        preferences.registerOnSharedPreferenceChangeListener(prefChangeListener)
    }

    fun clear() {
        preferences.unregisterOnSharedPreferenceChangeListener(prefChangeListener)
    }

}