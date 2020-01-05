package com.mobapproject.eventsroom.networks

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.mobapproject.eventsroom.data.User
import com.mobapproject.eventsroom.data.UserResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url
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

interface UserApiInterface {
    @GET()
    fun getUser(@Url userToken: String): Deferred<UserResponse>

    @POST("users")
    fun createUser(@Body user: User): Deferred<UserResponse>
}

object UserApi {
    val retrofitService: UserApiInterface by lazy {
        retrofit.create(UserApiInterface::class.java)
    }
}