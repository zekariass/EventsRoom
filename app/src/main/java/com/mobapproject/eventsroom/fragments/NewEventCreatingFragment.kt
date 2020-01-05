package com.mobapproject.eventsroom.fragments


import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.mobapproject.eventsroom.R
import com.mobapproject.eventsroom.data.Event
import com.mobapproject.eventsroom.databinding.FragmentNewEventCreatingBinding
import com.mobapproject.eventsroom.modelviews.NewEventViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class NewEventCreatingFragment : Fragment() {

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
    val timeFormatter = SimpleDateFormat("hh:mm")
    val today = Calendar.getInstance()
    val chosenDate = Calendar.getInstance()
    val chosenTime = Calendar.getInstance()
    lateinit var savedState: Bundle
    private lateinit var binding: FragmentNewEventCreatingBinding
    private var selectedImageUri: Uri = Uri.EMPTY
    var isUpdate = false
    var eventIdToUpdate = 0

    val vieModel: NewEventViewModel by lazy {
        ViewModelProviders.of(this).get(NewEventViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewEventCreatingBinding.inflate(inflater)

        val datePickerTextView = binding.eventDate.editText
        val timePickerTextView = binding.eventTime.editText
        val datePickerButton = binding.datePicker
        val timePickerButton = binding.timePicker

        val imagePicker = binding.imagePicker
        val upload = binding.eventUploadButton

        val arg = arguments
        if (arg != null) {
            val eventToBeUpdated = arguments!!.getParcelable("EVENT") as Event
            isUpdate = true
            eventIdToUpdate = eventToBeUpdated.id
            //Toast.makeText(context, eventToBeUpdated.country, Toast.LENGTH_LONG).show()
            setUpdateFragmentTextViews(eventToBeUpdated)
        }

        datePickerButton?.setOnClickListener {
            val datePicker = DatePickerDialog(
                context,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfmonth ->

                    chosenDate.set(Calendar.YEAR, year)
                    chosenDate.set(Calendar.MONTH, month)
                    chosenDate.set(Calendar.DAY_OF_MONTH, month)
                    val date = dateFormatter.format(chosenDate.time)
                    datePickerTextView?.setText(date)
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        timePickerButton.setOnClickListener {
            val timePicker = TimePickerDialog(
                context, TimePickerDialog.OnTimeSetListener { view, hoursOfDay, minute ->

                    chosenTime.set(Calendar.HOUR, hoursOfDay)
                    chosenTime.set(Calendar.MINUTE, minute)
                    val time = timeFormatter.format(chosenTime.time)
                    timePickerTextView?.setText(time)
                },
                today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE), false
            )
            timePicker.show()
        }

        imagePicker.setOnClickListener {
            getImageToUpload()
        }


        upload.setOnClickListener {
            if (isUpdate) {

                vieModel.updateExistingEvent(selectedImageUri, context!!, binding, eventIdToUpdate)

            } else {
                vieModel.uploadNewEvent(selectedImageUri, context!!, binding)
                selectedImageUri = Uri.EMPTY
            }
            //CLEAR TEXT FIELDS AFTER UPLOAD
            binding.eventTitle.editText?.text!!.clear()
            binding.eventDescription.editText?.text!!.clear()
            binding.eventCountry.editText?.text!!.clear()
            binding.eventCity.editText?.text!!.clear()
            binding.eventLocation.editText?.text!!.clear()
            binding.eventDate.editText?.text!!.clear()
            binding.eventTime.editText?.text!!.clear()
            binding.picName.text!!.clear()

            gotoEventListFragment()


        }

        return binding.root

    }

    private fun gotoEventListFragment() {
        val eventListFragment = EventListFragment()
        val fManager = activity!!.supportFragmentManager
        fManager.popBackStack()
        fManager.popBackStack()
        fManager.beginTransaction()
            .remove(this)
            .replace(R.id.nav_host_fragment, eventListFragment)
            .addToBackStack(eventListFragment.tag)
            .commit()

        val inputManager =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun setUpdateFragmentTextViews(eventToBeUpdated: Event) {
        binding.eventTitle.editText?.setText(eventToBeUpdated.title)
        binding.eventDescription.editText?.setText(eventToBeUpdated.description)
        binding.eventCountry.editText?.setText(eventToBeUpdated.country)
        binding.eventCity.editText?.setText(eventToBeUpdated.city)
        binding.eventLocation.editText?.setText(eventToBeUpdated.eventLocation)
        binding.eventDate.editText?.setText(eventToBeUpdated.eventDate)
        binding.eventTime.editText?.setText(eventToBeUpdated.eventTime.substring(11, 19))
    }

    private fun getImageToUpload() {
        val imageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(imageIntent, 10)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //savedState = Bundle()
        outState.putString("TITLE", binding.eventTitle.editText!!.text.toString())
        outState.putString("DESC", binding.eventDescription.editText!!.text.toString())
        outState.putString("CITY", binding.eventCity.editText!!.text.toString())
        outState.putString("COUNTRY", binding.eventCountry.editText!!.text.toString())
        outState.putString("LOCATION", binding.eventLocation.editText!!.text.toString())
        outState.putString("DATE", binding.eventDate.editText!!.text.toString())
        outState.putString("TIME", binding.eventTime.editText!!.text.toString())
        outState.putString("PICPATH", binding.picName!!.text.toString())

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            binding.eventTitle.editText!!.setText(savedInstanceState.getString("TITLE"))
            binding.eventDescription.editText!!.setText(savedInstanceState.getString("DESC"))
            binding.eventCity.editText!!.setText(savedInstanceState.getString("CITY"))
            binding.eventCountry.editText!!.setText(savedInstanceState.getString("COUNTRY"))
            binding.eventLocation.editText!!.setText(savedInstanceState.getString("LOCATION"))
            binding.eventDate.editText!!.setText(savedInstanceState.getString("DATE"))
            binding.eventTime.editText!!.setText(savedInstanceState.getString("TIME"))
            binding.picName.setText(savedInstanceState.getString("PICPATH"))
            selectedImageUri = vieModel.geSelectImgUri()!!
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 10 && resultCode == RESULT_OK && data != null) {
            //This  the is the Image URI
            selectedImageUri = data.data
            vieModel.setSelectedImgUri(selectedImageUri)
            var fileName = ""
            try {
                val cursor = activity!!.contentResolver.query(
                    selectedImageUri, null,
                    null, null, null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            binding.picName.append(fileName)
        }
    }


}
