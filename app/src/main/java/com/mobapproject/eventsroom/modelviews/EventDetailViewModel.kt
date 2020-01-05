package com.mobapproject.eventsroom.modelviews

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobapproject.eventsroom.R
import com.mobapproject.eventsroom.data.Event

class EventDetailViewModel(event: Event, app: Application) : AndroidViewModel(app) {

    private val _selectedEvent = MutableLiveData<Event>()
    val selectedEvent: LiveData<Event>
        get() = _selectedEvent

    init {
        _selectedEvent.value = event
    }

    /*val getFormattedTitle: String = app.applicationContext.getString(R.string.event_title, selectedEvent.value!!.title)
    val getFormattedDescription: String = app.applicationContext.getString(R.string.event_description, selectedEvent.value!!.description)
    val getFormattedCity: String = app.applicationContext.getString(R.string.event_city, selectedEvent.value!!.city)
    val getFormattedCountry: String = app.applicationContext.getString(R.string.event_country, selectedEvent.value!!.country)
    val getFormattedLocation: String = app.applicationContext.getString(R.string.event_location, selectedEvent.value!!.eventLocation)
    val getFormattedEventStatus: String = app.applicationContext.getString(R.string.event_event_status, selectedEvent.value!!.eventStatus)
    val getFormattedEventTime: String = app.applicationContext.getString(R.string.event_time, selectedEvent.value!!.eventTime)
    val getFormattedEventDate: String = app.applicationContext.getString(R.string.event_date, selectedEvent.value!!.eventDate)
    val getFormattedEventCreatedOn: String = app.applicationContext.getString(R.string.event_posted_on, selectedEvent.value!!.createdAt)
*/
    val getFormattedTitle: String = selectedEvent.value!!.title
    val getFormattedDescription: String = selectedEvent.value!!.description
    val getFormattedCity: String = selectedEvent.value!!.city
    val getFormattedCountry: String = selectedEvent.value!!.country
    val getFormattedLocation: String = selectedEvent.value!!.eventLocation
    val getFormattedEventStatus: String = selectedEvent.value!!.eventStatus
    val getFormattedEventTime: String = selectedEvent.value!!.eventTime.substring(11, 19)
    val getFormattedEventDate: String = selectedEvent.value!!.eventDate
    val getFormattedEventCreatedOn: String = selectedEvent.value!!.createdAt
    val getCurrentWeatherInCity: String =
        app.applicationContext.getString(R.string.city_weather, selectedEvent.value!!.city)


}