# HAZ-weather

Hazardous Weather, l'app de prévision météo pour sortir en toute sécurité !

L'application se compose de 3 écrans qui sont le **Splashscreen**, la **Homescreen** et la page **Settings**.


Pour requeter la météo, j'ai utilisé l'api https://darksky.net/dev/docs, avec Retrofit et Moshi pour parser.
Pour stocker la clef d'API, j'ai choisi d'utiliser le NDK et de la ranger dans un fichier C.


L'animation du Splashscreen est une animation lottie en JSON (http://airbnb.io/lottie/). L'application est sur un ton décalée (2 lunes, meteo hazardeuse, localisation futuriste, temparature en Kelvin ...) mais les données sources sont bien réelles (localisation + meteo).

La Home est en MVVM, avec les libraries jetpack de lifecycle (ViewModel / LiveData)
J'utilise RxJava pour faire les requêtes d'api, dans un répository qui s'appelle WeatherRepository.

J'ai ajouté quelques services Google comme le service de localisation, crashlytics, ou analytics.

les Settings de l'application sont gérés avec les SharedPreferences et Rx.

J'ai pris beaucoup de plaisir à développer ce projet, je pense que je vais continuer à le maintenir, mettre l'application en production, récupérer les différents crashs via crashlytics, améliorer les features et les settings, refactorer la Home.

Publique version :
https://play.google.com/store/apps/details?id=eu.gsegado.hazweather

Beta :
https://play.google.com/apps/testing/eu.gsegado.hazweather
