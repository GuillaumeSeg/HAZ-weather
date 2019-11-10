package eu.gsegado.hazweather.repository

import android.util.Log
import eu.gsegado.hazweather.BuildConfig
import eu.gsegado.hazweather.Constants
import eu.gsegado.hazweather.tools.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class WeatherRepository {

    companion object {
        init {
            System.loadLibrary("keys")
        }
    }

    external fun getDarkSkyApiKey(): String

    private val weatherService: WeatherService
    private val networkCompositeDisposable by lazy { CompositeDisposable() }

    init {
        val client = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            client.addInterceptor(logging)
        }
        weatherService = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client.build())
                .build()
                .create(WeatherService::class.java)
    }

    fun fetch(latitude: Float?, longitude: Float?) {
        networkCompositeDisposable.clear()
        Utils.safeLet(latitude, longitude) { latSafe, longSafe ->
            weatherService.getWeather(getDarkSkyApiKey(), latSafe, longSafe, "si")
                .map {
                    //behaviorSubject.onNext(it)
                    return@map it
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        //cache(it)
                    },
                    onError = {
                        it.message?.let { messageSafe ->
                            Log.e("WeatherRepository", messageSafe)
                        } ?: Log.e("WeatherRepository", "error fetch weather")
                    })
                .addTo(networkCompositeDisposable)
        }
    }

}