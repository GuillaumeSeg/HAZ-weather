package eu.gsegado.hazweather

object Constants {

    const val BASE_URL = "https://api.darksky.net/"

    const val PERMISSION_REQUEST_FINE_LOCATION = 55

    val labelSector = listOf("Zone", "Secteur", "Site", "Contrée", "Ceinture", "District")
    const val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    val listOfTips = listOf(
                        R.string.tips_1,
                        R.string.tips_2,
                        R.string.tips_3
                    )
}