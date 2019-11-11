package eu.gsegado.hazweather.tools

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import eu.gsegado.hazweather.Constants
import eu.gsegado.hazweather.R
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

object Utils {

    // SafeLet taking two parameters
    fun <T1: Any, T2: Any, R: Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2)->R?): R? =
        if (p1 != null && p2 != null) block(p1, p2) else null

    fun getRandomSectorLabel(): String {
        val rnd = (Constants.labelSector.indices).random()
        val letter = Constants.chars[(Constants.chars.indices).random()]
        val sectorLabel = Constants.labelSector[rnd]
        val number = (9..99).random()
        return "$sectorLabel $letter$number"
    }

    fun celsiusToKelvin(t: Double): Double {
        return t+273.15
    }

    fun displayTemperature(ctx: Context, temp: Double): String {
        val df = DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US))
        df.roundingMode = RoundingMode.CEILING
        return df.format(temp) +" "+ ctx.getString(R.string.unit_K)
    }

    fun fromHtml(str: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(str)
        }
    }
}