package com.mobapproject.eventsroom.networks

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.mobapproject.eventsroom.BuildConfig
import com.mobapproject.eventsroom.data.CurrentWeather
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


private val BASE_URL_API = "http://api.openweathermap.org/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private var okHttpClient = OkHttpClient.Builder()
    .connectTimeout(1, TimeUnit.MINUTES)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .addInterceptor(intercepter())
    .build()

private class intercepter : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (BuildConfig.DEBUG) {
            Log.e(javaClass.name, request.method() + " " + request.url())
            Log.e(javaClass.name, "" + request.header("Cookie"))
            val rb = request.body()
            val buffer = Buffer()
            rb?.writeTo(buffer)
            Log.i(javaClass.name, "Payload- " + buffer.readUtf8())
        }
        return chain.proceed(request)
    }

}
/*fun getIntercepter(): HttpLoggingInterceptor {
     val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return loggingInterceptor

}*/


private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL_API)
    .client(okHttpClient)
    .build()


interface CurrentWetherInterface {
    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") appid: String
    ): Deferred<CurrentWeather>
}

object WeatherApi {
    val retrofitWeatherService: CurrentWetherInterface by lazy {
        retrofit.create(CurrentWetherInterface::class.java)
    }
}