package eu.gsegado.hazweather

object Constants {

    const val BASE_URL = "https://api.darksky.net/"

    const val PERMISSION_REQUEST_FINE_LOCATION = 55

    val labelSector = listOf("Zone", "Secteur", "Site", "Contrée", "Ceinture", "District")
    const val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    val listOfTips = listOf(
                        R.string.tips_1,
                        R.string.tips_2,
                        R.string.tips_3,
                        R.string.tips_4,
                        R.string.tips_5,
                        R.string.tips_6,
                        R.string.tips_7
                    )

    enum class UnitSystem(val unit: String) {
        KELVIN("K"),
        FAHRENHEIT("F");

        companion object {
            fun from(findValue: String): UnitSystem = UnitSystem.values().first { it.unit == findValue }
        }
    }
}