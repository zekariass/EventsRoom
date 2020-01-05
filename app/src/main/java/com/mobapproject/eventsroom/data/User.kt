package com.mobapproject.eventsroom.data

import com.squareup.moshi.Json

data class User(
    @Json(name = "user_name") val userName: String,
    val uid: String,
    val email: String
)