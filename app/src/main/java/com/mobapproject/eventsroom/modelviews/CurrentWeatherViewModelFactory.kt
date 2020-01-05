package com.mobapproject.eventsroom.modelviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CurrentWeatherViewModelFactory(val cityOfEvent: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrentWetherViewModel::class.java)) {
            return CurrentWetherViewModel(cityOfEvent) as T
        } else {
            throw IllegalArgumentException("Unknow ViewModel Class")
        }
    }

}