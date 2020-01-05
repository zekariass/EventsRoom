package com.mobapproject.eventsroom.data

import com.squareup.moshi.Json

data class UserInterestResponse(
    val id: Int,
    @Json(name = "event_id") val eventId: Int,
    @Json(name = "user_id") val userId: Int,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String
)