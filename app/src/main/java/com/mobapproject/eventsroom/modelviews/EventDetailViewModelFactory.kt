package com.mobapproject.eventsroom.modelviews

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mobapproject.eventsroom.data.Event

class EventDetailViewModelFactory(
    private val event: Event,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(EventDetailViewModel::class.java)) {
            return EventDetailViewModel(event, application) as T
        } else {
            throw IllegalArgumentException("Unknown viewmodel class")
        }
    }

}