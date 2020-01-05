package com.mobapproject.eventsroom.modelviews


import android.app.Application
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.PopupWindow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobapproject.eventsroom.R
import com.mobapproject.eventsroom.data.Event
import com.mobapproject.eventsroom.networks.EventApi
import kotlinx.android.synthetic.main.about_layout.view.*
import kotlinx.android.synthetic.main.filter_by_city_and_country_layout.view.*
import kotlinx.android.synthetic.main.my_events_filter_layout.view.*
import kotlinx.coroutines.*

enum class EventLoadingStatus { LOADING, ERROR, DONE }

class EventListViewModel(app: Application) : AndroidViewModel(app) {

    private val _eventResponses = MutableLiveData<List<Event>>()
    val eventResponses: LiveData<List<Event>>
        get() = _eventResponses


    private val _responseStatus = MutableLiveData<EventLoadingStatus>()
    val responseStatus: LiveData<EventLoadingStatus>
        get() = _responseStatus


    private val _navigateToEventDetailFragmetValue = MutableLiveData<Event>()
    val navigateToEventDetailFragmetValue: LiveData<Event>
        get() = _navigateToEventDetailFragmetValue

    private val _updateResponses = MutableLiveData<Event>()
    val updateResponses: LiveData<Event>
        get() = _updateResponses

    private val eventCoroutineJob = Job()

    private val eventCoroutineScope = CoroutineScope(eventCoroutineJob + Dispatchers.Main)

    init {
        // retrieveEventsInBackground(currCity)
    }

    fun retrieveEventsInBackground(currCity: String) {
        var city = ""
        try {
            city = currCity.substring(0, currCity.length - 2)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
        eventCoroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            var getPropertiesDeferred = EventApi.retrofitService.getEventByCity(city)
            try {
                _responseStatus.value = EventLoadingStatus.LOADING
                val listResult = getPropertiesDeferred.await()
                _eventResponses.value = listResult
                _responseStatus.value = EventLoadingStatus.DONE
                Log.i("RETROFIT ZEK: ", _eventResponses.value.toString())
            } catch (e: Exception) {
                _responseStatus.value = EventLoadingStatus.ERROR
                _eventResponses.value = ArrayList()
                //Log.i("RETROFIT STAT: ", e.toString())
                // Log.i("RETROFIT RES: ", _eventResponses.value.toString())

            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        eventCoroutineJob.cancel()
    }

    fun displayEvenetDetailFragmet(event: Event) {
        _navigateToEventDetailFragmetValue.value = event
    }

    fun displayEvenetDetailFragmetComplete() {
        _navigateToEventDetailFragmetValue.value = null
    }

    fun leadEventsByCityAndCountry(context: Context?) {
        val layoutInflater: LayoutInflater =
            context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View =
            layoutInflater.inflate(R.layout.filter_by_city_and_country_layout, null)
        val popupWindow: PopupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isTouchable = true
        popupWindow.isFocusable = true

        if (Build.VERSION.SDK_INT >= 21) {
            popupWindow.elevation = 5.0f
        }
        val point = Point()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            windowManager.defaultDisplay.getRealSize(point)
        else
            windowManager.defaultDisplay.getSize(point)
        val screenWidth = point.x
        val screenHeight = point.y

        popupWindow.width = screenWidth / 2 + screenWidth / 3
        popupWindow.height = screenHeight / 4

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        popupView.search_by_city_and_country_button.setOnClickListener {
            val city = popupView.filter_city_textview.text.toString()
            val country = popupView.filter_country_textview.text.toString()
            getEventsByCityOrCountry(city, country)
            popupWindow.dismiss()
        }
    }

    private fun getEventsByCityOrCountry(
        city: String,
        country: String
    ) {
        eventCoroutineScope.launch {
            // Get the Deferred object for our Retrofit request

            var filterCity = city
            var filterCountry = country

            var getPropertiesDeferred: Deferred<List<Event>>

            if (filterCountry.length == 0 && filterCity.length > 0) {
                getPropertiesDeferred = EventApi.retrofitService.getEventByCity(filterCity)
                filterCity = ""
            } else if (filterCity.length == 0 && filterCountry.length > 0) {
                getPropertiesDeferred = EventApi.retrofitService.getEventByCountry(filterCountry)
                filterCountry = ""
            } else if (filterCity.length > 0 && filterCountry.length > 0) {
                getPropertiesDeferred =
                    EventApi.retrofitService.getEventByCityAndCountry(filterCity, filterCountry)
                filterCity = ""
                filterCountry = ""
            } else {
                getPropertiesDeferred = EventApi.retrofitService.getEvents()
            }

            try {
                _responseStatus.value = EventLoadingStatus.LOADING
                val listResult = getPropertiesDeferred.await()
                _eventResponses.value = listResult
                _responseStatus.value = EventLoadingStatus.DONE
            } catch (e: Exception) {
                _responseStatus.value = EventLoadingStatus.ERROR
                _eventResponses.value = ArrayList()

            }
        }
    }

    fun getMyEvents(context: Context?) {
        val layoutInflater: LayoutInflater =
            context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = layoutInflater.inflate(R.layout.my_events_filter_layout, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isTouchable = true
        popupWindow.isFocusable = true

        if (Build.VERSION.SDK_INT >= 21) {
            popupWindow.elevation = 30.0f
        }

        val point = Point()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            windowManager.defaultDisplay.getRealSize(point)
        else
            windowManager.defaultDisplay.getSize(point)
        val screenWidth = point.x
        val screenHeight = point.y

        popupWindow.width = screenWidth / 2 + screenWidth / 3
        popupWindow.height = screenHeight / 4

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        var open = "" //Open state events
        var closed = "" //Close state events
        //Open check box listener
        popupView.open_checkbox.setOnClickListener {
            if (it is CheckBox) {
                if (it.isChecked) {
                    open = "open"
                } else {
                    open = ""
                }
            }
        }

        //Closed check box listener
        popupView.close_checkbox.setOnClickListener {
            if (it is CheckBox) {
                if (it.isChecked) {
                    closed = "closed"
                } else {
                    closed = ""
                }
            }
        }

        popupView.get_my_events_button.setOnClickListener {
            if (open.length > 0 && closed.length == 0) {
                loadOpenOrClosedMyEvents(open, context)
            } else if (open.length == 0 && closed.length > 0) {
                loadOpenOrClosedMyEvents(closed, context)
            } else if (open.length > 0 && closed.length > 0) {
                loadAllMyEvents(context)
            }

            popupWindow.dismiss()
        }

    }

    private fun loadAllMyEvents(context: Context) {

        val userPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val userId = userPreferences.getInt("ID", 0)

        eventCoroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            var getPropertiesDeferred = EventApi.retrofitService.getEventByUser(userId)
            try {
                _responseStatus.value = EventLoadingStatus.LOADING
                val listResult = getPropertiesDeferred.await()
                _eventResponses.value = listResult
                _responseStatus.value = EventLoadingStatus.DONE
            } catch (e: Exception) {
                _responseStatus.value = EventLoadingStatus.ERROR
                _eventResponses.value = ArrayList()
            }
        }
    }

    private fun loadOpenOrClosedMyEvents(eventStatus: String, context: Context) {

        val userPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val userId = userPreferences.getInt("ID", 0)

        eventCoroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            var getPropertiesDeferred =
                EventApi.retrofitService.getEventByUserAndStatus(userId, eventStatus)
            try {
                _responseStatus.value = EventLoadingStatus.LOADING
                val listResult = getPropertiesDeferred.await()
                _eventResponses.value = listResult
                _responseStatus.value = EventLoadingStatus.DONE
            } catch (e: Exception) {
                _responseStatus.value = EventLoadingStatus.ERROR
                _eventResponses.value = ArrayList()
            }
        }
    }

    fun showAboutPopupWindow(context: Context?) {

        val layoutInflater: LayoutInflater =
            context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = layoutInflater.inflate(R.layout.about_layout, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isTouchable = true
        popupWindow.isFocusable = true

        if (Build.VERSION.SDK_INT >= 21) {
            popupWindow.elevation = 30.0f
        }

        if (Build.VERSION.SDK_INT >= 21) {
            popupWindow.elevation = 20.0f
        }
        val point = Point()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            windowManager.defaultDisplay.getRealSize(point)
        else
            windowManager.defaultDisplay.getSize(point)

        popupWindow.width = point.x
        popupWindow.height = (point.y) / 2

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        popupView.cancel_about_popup_button.setOnClickListener {
            popupWindow.dismiss()
        }
    }

}