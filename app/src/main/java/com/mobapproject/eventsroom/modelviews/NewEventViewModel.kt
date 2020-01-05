package com.mobapproject.eventsroom.modelviews

/*import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody*/
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.loader.content.CursorLoader
import com.mobapproject.eventsroom.databinding.FragmentNewEventCreatingBinding
import com.mobapproject.eventsroom.networks.EventApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class NewEventViewModel : ViewModel() {

    private lateinit var userPref: SharedPreferences
    lateinit var eventTitle: String
    lateinit var eventDescription: String
    lateinit var eventLocation: String
    lateinit var eventCity: String
    lateinit var eventCountry: String
    lateinit var eventDate: String
    lateinit var eventTime: String
    lateinit var eventStatus: String
    var eventUserId = "1"
    lateinit var eventImage: String
    lateinit var selectedImageUri: Uri

    lateinit var path: File
    lateinit var requestBody: RequestBody
    lateinit var multipartEventImage: MultipartBody.Part
    lateinit var title: RequestBody
    lateinit var description: RequestBody
    lateinit var country: RequestBody
    lateinit var city: RequestBody
    lateinit var location: RequestBody
    lateinit var date: RequestBody
    lateinit var time: RequestBody
    lateinit var status: RequestBody
    lateinit var user: RequestBody

    fun setSelectedImgUri(uri: Uri) {
        selectedImageUri = uri
    }

    fun geSelectImgUri(): Uri? {
        return selectedImageUri
    }

    private val eventUploadJob = Job()
    private val eventUploadScope = CoroutineScope(eventUploadJob + Dispatchers.Main)


    fun uploadNewEvent(
        imageUri: Uri,
        context: Context,
        binding: FragmentNewEventCreatingBinding
    ) {

        getFormData(context, binding)
        uploadEventInTheBackground(imageUri, context)
    }

    fun updateExistingEvent(
        selectedImageUri: Uri,
        context: Context,
        binding: FragmentNewEventCreatingBinding,
        eventIdToUpdate: Int
    ) {

        getFormData(context, binding)
        updateEventInTheBackground(selectedImageUri, context, eventIdToUpdate)

    }

    private fun getFormData(
        context: Context,
        binding: FragmentNewEventCreatingBinding
    ) {
        userPref = PreferenceManager.getDefaultSharedPreferences(context)
        val userid = userPref.getInt("ID", 0)
        val uname = userPref.getString("USER_NAME", null)
        eventUserId = userid.toString()

        eventTitle = binding.eventTitle.editText?.text.toString()
        eventDescription = binding.eventDescription.editText?.text.toString()
        eventLocation = binding.eventLocation.editText?.text.toString()
        eventCity = binding.eventCity.editText?.text.toString()
        eventCountry = binding.eventCountry.editText?.text.toString()
        eventDate = binding.eventDate.editText?.text.toString()
        eventTime = binding.eventTime.editText?.text.toString() + ":00"
        eventStatus = "open"
    }

    private fun uploadEventInTheBackground(
        imageUri: Uri,
        context: Context
    ) {

        createRequestBodyToUpload(context, imageUri)

        eventUploadScope.launch {
            val eventCreateTask = EventApi.retrofitService.postEvent(
                title
                ,
                description,
                country,
                city,
                location,
                date,
                time,
                status,
                user,
                multipartEventImage
            )
            try {
                val eventCreateTaskResponse = eventCreateTask.await()
                val responseBody = eventCreateTaskResponse
                Log.i("POST RESP: ", responseBody.responseStr)
            } catch (e: Exception) {
                val error = e.message
                Log.i("POST ERROR: ", e.message)
                e.printStackTrace()
            }

        }

    }

    val eventUpdateJob = Job()
    val eventUpdateScope = CoroutineScope(eventUpdateJob + Dispatchers.Main)

    private fun updateEventInTheBackground(
        selectedImageUri: Uri,
        context: Context,
        eventIdToUpdate: Int
    ) {
        createRequestBodyToUpload(context, selectedImageUri)
        eventUpdateScope.launch {
            if (selectedImageUri != Uri.EMPTY) {
                val updateEventTask = EventApi.retrofitService.updateAllAttr(
                    eventIdToUpdate, title
                    , description, country, city, location, date, time, multipartEventImage
                )
                try {
                    val updateEventTaskResponse = updateEventTask.await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                val updateEventTask = EventApi.retrofitService.updateWithOutImage(
                    eventIdToUpdate, title
                    , description, country, city, location, date, time
                )
                try {
                    val updateEventTaskResponse = updateEventTask.await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


        }
    }

    private fun createRequestBodyToUpload(context: Context, imageUri: Uri) {
        if (imageUri != Uri.EMPTY) {
            path = File(getAbsolutePathFromUri(context, imageUri))
            requestBody = RequestBody.create(MediaType.parse("image/*"), path)
            multipartEventImage =
                MultipartBody.Part.createFormData("event[eventimage]", path.name, requestBody)
        }
        title = RequestBody.create(MediaType.parse("multipart/form-data"), eventTitle)
        description = RequestBody.create(MediaType.parse("multipart/form-data"), eventDescription)
        country = RequestBody.create(MediaType.parse("multipart/form-data"), eventCountry)
        city = RequestBody.create(MediaType.parse("multipart/form-data"), eventCity)
        location = RequestBody.create(MediaType.parse("multipart/form-data"), eventLocation)
        date = RequestBody.create(MediaType.parse("multipart/form-data"), eventDate)
        time = RequestBody.create(MediaType.parse("multipart/form-data"), eventTime)
        status = RequestBody.create(MediaType.parse("multipart/form-data"), eventStatus)
        user = RequestBody.create(MediaType.parse("multipart/form-data"), eventUserId)
    }

    private fun getAbsolutePathFromUri(context: Context, imageUri: Uri): String? {

        val mediaData = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(context!!, imageUri, mediaData, null, null, null)
        val cursor = loader.loadInBackground()
        val col_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val resultPath = cursor.getString(col_index)
        cursor.close()
        return resultPath

    }

    override fun onCleared() {
        super.onCleared()
        eventUploadJob.cancel()
    }
}