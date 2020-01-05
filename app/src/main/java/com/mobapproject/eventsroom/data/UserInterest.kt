package com.mobapproject.eventsroom.data

import com.squareup.moshi.Json

data class UserInterest(
    @Json(name = "event_id") val eventId: Int,
    @Json(name = "user_id") val userId: Int
)