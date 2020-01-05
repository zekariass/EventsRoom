package com.mobapproject.eventsroom.data

// get Number of interests for an event
data class EventInterests(
    val count: Int, //Number of interests of an event
    val user: Int  //Whether a user is already interested or not, 1 interested, 0 not
)