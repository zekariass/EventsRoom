package com.mobapproject.eventsroom.modelviews

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobapproject.eventsroom.data.CurrentWeather
import com.mobapproject.eventsroom.networks.WeatherApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CurrentWetherViewModel(
    val cityOfEvent: String
) : ViewModel() {

    private val _currentWeather = MutableLiveData<CurrentWeather>()
    val currentWeather: LiveData<CurrentWeather>
        get() = _currentWeather

    private val _iconUrl = MutableLiveData<String>()
    val iconUrl: LiveData<String>
        get() = _iconUrl


    val city = cityOfEvent
    private val weatherJob = Job()
    private val weatherShowScope = CoroutineScope(weatherJob + Dispatchers.Main)


    init {
        startCourotineForfetchingWeather()
    }


    private fun startCourotineForfetchingWeather() {

        weatherShowScope.launch {
            val callWeatherApi = WeatherApi.retrofitWeatherService.getCurrentWeather(
                cityOfEvent,
                "d30b80806c50902f85b7b3924dfcea33"
            )
            try {
                val weatResult = callWeatherApi.await()
                _currentWeather.value = weatResult
                val iconName = weatResult.weather[0].icon
                _iconUrl.value =
                    String.format("http://openweathermap.org/img/wn/%s@2x.png", iconName)
                Log.i("WEATHER RESPONSE", _currentWeather.value.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("WEATHER ERROR", e.message)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        weatherJob.cancel()
    }
}