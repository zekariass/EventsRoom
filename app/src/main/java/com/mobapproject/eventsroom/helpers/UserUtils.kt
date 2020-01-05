package com.mobapproject.eventsroom.helpers

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.mobapproject.eventsroom.data.User
import com.mobapproject.eventsroom.data.UserResponse
import com.mobapproject.eventsroom.networks.UserApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object UserUtils {

    private val userJob = Job()
    private val userCoroutineScope = CoroutineScope(userJob + Dispatchers.Main)
    private lateinit var foundUser: UserResponse

    fun getUserInfo(context: Context, loggedUser: User) {
        checkIfUserExist(context, loggedUser)
    }

    fun checkIfUserExist(context: Context, loggedUser: User) {
        userCoroutineScope.launch {
            val USER_URL = "https://eventnotifierjson2.herokuapp.com/users/"
            val userid = loggedUser.uid
            val FULL_URL = USER_URL + userid.toLowerCase()
            val request = UserApi.retrofitService.getUser(FULL_URL)
            try {
                val userid = request.await()

                //val preferences = context.getSharedPreferences("CURRENT_LOGEDIN_USER", 0)
                saveToPreference(context, userid)

                Log.i("USER RESPONSE: ", userid.toString())
            } catch (e: Exception) {
                Log.i("USER RETRIEVAL ERROR: ", e.message)
                if (e.message!!.contains("404")) {
                    createNewUser(context, loggedUser)
                }
            }
        }


    }

    fun createNewUser(context: Context, user: User) {
        userCoroutineScope.launch {
            val request = UserApi.retrofitService.createUser(user)
            try {
                val response = request.await()

                saveToPreference(context, response)

                Log.i("USER ADDED: ", response.toString())

            } catch (e: Exception) {
                Log.i("USER CREATION ERROR: ", e.message)
            }
        }
    }

    private fun saveToPreference(
        context: Context,
        response: UserResponse
    ) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefEditor = preferences.edit()
        prefEditor.putInt("ID", response.id)
        prefEditor.putString("USER_NAME", response.userName)
        prefEditor.putString("EMAIL", response.email)
        prefEditor.putString("UID", response.uid)
        prefEditor.putString("SLUG", response.slug)
        prefEditor.apply()
    }
}