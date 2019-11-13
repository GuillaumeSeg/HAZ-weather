package eu.gsegado.hazweather.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import eu.gsegado.hazweather.R

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        key?.let {
            if (it == "units") {
                sharedPreferences?.getString(key, "K")?.let { unitChoice ->
                    val unitsPref = findPreference<ListPreference>(it)
                    when (unitChoice) {
                        "K" -> unitsPref?.summary = getString(R.string.settings_kelvin)
                        "F" -> unitsPref?.summary = getString(R.string.settings_fahrenheit)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }
}