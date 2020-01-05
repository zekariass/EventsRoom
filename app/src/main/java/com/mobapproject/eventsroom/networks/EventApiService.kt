package com.mobapproject.eventsroom.networks


import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.mobapproject.eventsroom.data.DeleteResponse
import com.mobapproject.eventsroom.data.Event
import com.mobapproject.eventsroom.data.EventUploadResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


private val BASE_URL = "https://eventnotifierjson2.herokuapp.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

//========================================================================
private var okHttpClient = OkHttpClient.Builder()
    .connectTimeout(1, TimeUnit.MINUTES)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()


interface EventApiInterface {
    @GET("events")
    fun getEvents(): Deferred<List<Event>>

    @GET("events")
    fun getEventByCity(@Query("city") city: String): Deferred<List<Event>>

    @GET("events")
    fun getEventByCountry(@Query("country") country: String): Deferred<List<Event>>

    @GET("events")
    fun getEventByCityAndCountry(
        @Query("city") city: String,
        @Query("country") country: String
    ): Deferred<List<Event>>

    @GET("events")
    fun getEventByUser(@Query("user_id") userId: Int): Deferred<List<Event>>

    @GET("events")
    fun getEventByUserAndStatus(
        @Query("user_id") userId: Int,
        @Query("event_status") event_status: String
    ): Deferred<List<Event>>

    @Multipart
    @POST("events")
    fun postEvent(
        @Part("event[title]") title: RequestBody,
        @Part("event[description]") description: RequestBody,
        @Part("event[country]") country: RequestBody,
        @Part("event[city]") city: RequestBody,
        @Part("event[event_location]") loc: RequestBody,
        @Part("event[event_date]") date: RequestBody,
        @Part("event[event_time]") time: RequestBody,
        @Part("event[event_status]") status: RequestBody,
        @Part("event[user_id]") uid: RequestBody,
        @Part eventimage: MultipartBody.Part
    ): Deferred<EventUploadResponse>

    @DELETE("events/{id}")
    fun deleteEvent(@Path("id") id: Int): Deferred<DeleteResponse>

    @Multipart
    @PUT("events/{id}")
    fun updateAllAttr(
        @Path("id") id: Int,
        @Part("event[title]") title: RequestBody,
        @Part("event[description]") description: RequestBody,
        @Part("event[country]") country: RequestBody,
        @Part("event[city]") city: RequestBody,
        @Part("event[event_location]") loc: RequestBody,
        @Part("event[event_date]") date: RequestBody,
        @Part("event[event_time]") time: RequestBody,
        @Part eventimage: MultipartBody.Part
    ): Deferred<EventUploadResponse>

    @Multipart
    @PUT("events/{id}")
    fun updateWithOutImage(
        @Path("id") id: Int,
        @Part("event[title]") title: RequestBody,
        @Part("event[description]") description: RequestBody,
        @Part("event[country]") country: RequestBody,
        @Part("event[city]") city: RequestBody,
        @Part("event[event_location]") loc: RequestBody,
        @Part("event[event_date]") date: RequestBody,
        @Part("event[event_time]") time: RequestBody
    ): Deferred<EventUploadResponse>

    @Multipart
    @PUT("events/{id}")
    fun updateEventStatusForEvent(
        @Path("id") eventId: Int,
        @Part("event[event_status]") statusValue: RequestBody
    ): Deferred<Event>

}

object EventApi {

    val retrofitService: EventApiInterface by lazy {
        retrofit.create(EventApiInterface::class.java)
    }
}