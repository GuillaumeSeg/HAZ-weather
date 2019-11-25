package eu.gsegado.hazweather

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import com.norbsoft.typefacehelper.TypefaceCollection
import com.norbsoft.typefacehelper.TypefaceHelper
import eu.gsegado.hazweather.manager.LocaleManager

class GlobalApplication : Application() {

    companion object {
        var localeManager: LocaleManager? = null
    }

    override fun onCreate() {
        super.onCreate()

        localeManager?.setLocale(this)

        val customTypeface = TypefaceCollection.Builder()
            .set(Typeface.NORMAL, Typeface.createFromAsset(assets, "fonts/Metropolis-Regular.otf"))
            .set(Typeface.BOLD, Typeface.createFromAsset(assets, "fonts/Metropolis-Bold.otf"))
            .create()
        TypefaceHelper.init(customTypeface)
    }

    override fun attachBaseContext(base: Context) {
        localeManager = LocaleManager(base)
        super.attachBaseContext(localeManager?.setLocale(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeManager?.setLocale(this)
    }
}