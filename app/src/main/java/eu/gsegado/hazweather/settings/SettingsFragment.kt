package eu.gsegado.hazweather.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import eu.gsegado.hazweather.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

}