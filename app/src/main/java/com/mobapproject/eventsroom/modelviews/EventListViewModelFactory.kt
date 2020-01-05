package com.mobapproject.eventsroom.modelviews

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EventListViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventListViewModel::class.java)) {
            return EventListViewModel(app) as T
        } else {
            throw IllegalArgumentException("Unknown class")
        }
    }

}
