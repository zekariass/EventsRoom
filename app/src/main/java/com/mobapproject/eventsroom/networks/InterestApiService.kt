package com.mobapproject.eventsroom.networks

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.mobapproject.eventsroom.data.EventInterests
import com.mobapproject.eventsroom.data.UserInterest
import com.mobapproject.eventsroom.data.UserInterestResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private val BASE_URL = "https://eventnotifierjson2.herokuapp.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val okHttpClient = OkHttpClient.Builder()
    .writeTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .connectTimeout(1, TimeUnit.MINUTES)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

interface InterestApiInterface {

    //Create ne interest for the event pressed by the suer
    @POST("interests")
    fun postUserIneterst(@Body interest: UserInterest): Deferred<UserInterestResponse>

    //The folowing interface gets the new number of interests for the event
    @GET("interests")
    fun getEventInterests(
        @Query("user_id") userId: Int,
        @Query("event_id") eventId: Int
    ): Deferred<EventInterests>

    //The folowing interface deletes the interest of the current user and gets the new number of interests for the event
    @GET("interests")
    fun deleteUserInterstAndGet(
        @Query("user_id") userId: Int,
        @Query("event_id") eventId: Int,
        @Query("delid") delId: Int
    ): Deferred<EventInterests>
}

object InterestApi {
    val retrofitService: InterestApiInterface by lazy {
        retrofit.create(InterestApiInterface::class.java)
    }
}