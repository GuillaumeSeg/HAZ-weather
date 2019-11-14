package eu.gsegado.hazweather

import android.app.Application
import android.content.res.Configuration
import android.graphics.Typeface
import com.norbsoft.typefacehelper.TypefaceCollection
import com.norbsoft.typefacehelper.TypefaceHelper

class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val customTypeface = TypefaceCollection.Builder()
            .set(Typeface.NORMAL, Typeface.createFromAsset(assets, "fonts/Metropolis-Regular.otf"))
            .set(Typeface.BOLD, Typeface.createFromAsset(assets, "fonts/Metropolis-Bold.otf"))
            .create()
        TypefaceHelper.init(customTypeface)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}