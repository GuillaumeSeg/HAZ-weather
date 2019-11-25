package eu.gsegado.hazweather.manager

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import androidx.preference.PreferenceManager
import java.util.*

class LocaleManager(context: Context) {

    companion object {
        const val KEY_LANG = "lang"
    }

    private val prefs: SharedPreferences? = PreferenceManager.getDefaultSharedPreferences(context)

    fun setLocale(ctx: Context): Context {
        return updateResources(ctx, getLanguage())
    }

    fun setNewLocale(ctx: Context, language: String): Context {
        persistLanguage(language)
        return updateResources(ctx, language)
    }

    private fun getLanguage(): String {
        return prefs?.getString(KEY_LANG, "fr") ?: "fr"
    }

    private fun persistLanguage(language: String) {
        prefs?.edit()?.putString(KEY_LANG, language)?.apply()
    }

    private fun updateResources(ctx: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val res: Resources = ctx.resources
        val config = Configuration(res.configuration)

        val newCtx: Context
        /*newCtx = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)

            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)

            ctx.createConfigurationContext(config)
        } else {
            config.setLocale(locale)
            ctx.createConfigurationContext(config)
        }*/

        config.setLocale(locale)
        newCtx = ctx.createConfigurationContext(config)

        return newCtx
    }

}