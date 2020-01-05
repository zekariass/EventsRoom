package com.mobapproject.eventsroom.data

import com.squareup.moshi.Json

data class UserResponse(
    val id: Int,
    @Json(name = "user_name") val userName: String,
    val uid: String,
    val email: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String,
    val slug: String
)