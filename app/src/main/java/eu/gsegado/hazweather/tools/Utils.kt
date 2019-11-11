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

    fun getPhase(phase: Float): Pair<Int, Int>? {
        return when (phase) {
            in 0.0f..0.1249f -> {
                Pair(R.string.moon_0, R.drawable.moon_0)
            }
            in 0.125f..0.249f -> {
                Pair(R.string.moon_1, R.drawable.moon_1)
            }
            in 0.25f..0.3749f -> {
                Pair(R.string.moon_2, R.drawable.moon_2)
            }
            in 0.375f..0.49f -> {
                Pair(R.string.moon_3, R.drawable.moon_3)
            }
            in 0.5f..0.6249f -> {
                Pair(R.string.moon_4, R.drawable.moon_4)
            }
            in 0.625f..0.749f -> {
                Pair(R.string.moon_5, R.drawable.moon_5)
            }
            in 0.75f..0.8749f -> {
                Pair(R.string.moon_6, R.drawable.moon_6)
            }
            in 0.875f..1.0f -> {
                Pair(R.string.moon_7, R.drawable.moon_7)
            }
            else -> {
                null
            }
        }
    }
}