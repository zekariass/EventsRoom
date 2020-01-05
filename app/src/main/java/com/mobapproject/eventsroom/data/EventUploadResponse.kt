package com.mobapproject.eventsroom.data

import com.squareup.moshi.Json


data class EventUploadResponse(
    @Json(name = "id")
    val eventId: Int,
    @Json(name = "response")
    val responseStr: String
) {


}