package com.mobapproject.eventsroom.data


import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val country: String,
    val city: String,
    @Json(name = "event_location") val eventLocation: String,
    @Json(name = "event_date") val eventDate: String,
    @Json(name = "event_time") val eventTime: String,
    @Json(name = "event_status") val eventStatus: String,
    @Json(name = "user_id") val userId: Int,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String,
    val eventimage: EventImage
) : Parcelable

@Parcelize
data class EventImage(
    val url: String
) : Parcelable

/*
data class Event(
    val id: String,
    @Json(name = "img_src") val imgSrcUrl: String,
    val type: String,
    val price: Double){}
*/
