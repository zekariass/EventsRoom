package com.mobapproject.eventsroom.helpers

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mobapproject.eventsroom.fragments.EventListFragment
import com.mobapproject.eventsroom.modelviews.EventListViewModel
import com.mobapproject.eventsroom.modelviews.EventListViewModelFactory
import java.util.*

class CurrentLocationProvider {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var city = ""
    private lateinit var eventListFragment: EventListFragment
    fun getCurrentLocation(
        context: Context,
        eventListFragment: EventListFragment
    ): String {
        this.eventListFragment = eventListFragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                val latitude = it.latitude
                val logitude = it.longitude
                //  Toast.makeText(context, "Latitude: "+latitude + " Longitude: "+logitude, Toast.LENGTH_LONG).show()
                reverseGeocodeLocation(latitude, logitude, context)
            }
        }

        return city

    }

    private fun reverseGeocodeLocation(
        latitude: Double,
        logitude: Double,
        context: Context
    ) {

        var city = "";
        val reverseGeocoder = Geocoder(context, Locale.getDefault())

        var adresses: List<Address> = emptyList()
        try {
            adresses = reverseGeocoder.getFromLocation(latitude, logitude, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (adresses.isEmpty()) {
            city = ""
            Toast.makeText(context, "NO ADDRESS FOUND!!", Toast.LENGTH_LONG).show()
        } else {
            city = adresses.get(0).locality
        }

        val app = requireNotNull(eventListFragment.activity!!.application)

        val viewModelFactory = EventListViewModelFactory(app)
        var eventListViewModel = ViewModelProviders.of(eventListFragment, viewModelFactory)
            .get(EventListViewModel::class.java)
        eventListViewModel.retrieveEventsInBackground(city)
    }
}