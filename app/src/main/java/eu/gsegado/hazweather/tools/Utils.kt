package eu.gsegado.hazweather.tools

import eu.gsegado.hazweather.Constants

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
}