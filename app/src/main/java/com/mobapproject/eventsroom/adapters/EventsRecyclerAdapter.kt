package com.mobapproject.eventsroom.adapters

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.widget.*
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobapproject.eventsroom.R
import com.mobapproject.eventsroom.data.Event
import com.mobapproject.eventsroom.data.UserInterest
import com.mobapproject.eventsroom.databinding.EventsListLayoutBinding
import com.mobapproject.eventsroom.fragments.EventListFragment
import com.mobapproject.eventsroom.fragments.NewEventCreatingFragment
import com.mobapproject.eventsroom.modelviews.EventListViewModel
import com.mobapproject.eventsroom.modelviews.EventListViewModelFactory
import com.mobapproject.eventsroom.networks.EventApi
import com.mobapproject.eventsroom.networks.InterestApi
import kotlinx.android.synthetic.main.change_status_popup_window.view.*
import kotlinx.android.synthetic.main.events_list_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody

class EventsRecyclerAdapter(
    private val onclickListner: OnclickListner,
    eventListFragment: EventListFragment,
    context: Context,
    activity: FragmentActivity?
) : ListAdapter<Event, EventsRecyclerAdapter.EventViewHolder>(DiffItemCallback) {

    val activity = activity
    val eventListFragment = eventListFragment
    val context = context
    val userAndInterestMapToAnEvent = HashMap<Int, Int>()

    companion object DiffItemCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {

        return EventViewHolder(EventsListLayoutBinding.inflate(LayoutInflater.from(parent.context)))//.inflate(R.layout.events_list_layout, parent, false))
    }


    val app = requireNotNull(activity!!.application)

    val viewModelFactory = EventListViewModelFactory(app)
    val eventListViewModel = ViewModelProviders.of(eventListFragment, viewModelFactory)
        .get(EventListViewModel::class.java)

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {

        val event = getItem(position)
        holder.itemView.setOnClickListener {
            onclickListner.onClick(event)
        }

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val userId = preferences.getInt("ID", 0)

        moreOptionPopupMenu(userId, event, holder, position)

        //When interest_linear_layout is pressed, check is the user has already interested to the event
        //If interested already, delete the interest, otherwise create new interest
        holder.itemView.interest_linear_layout.setOnClickListener {
            val userIntererstForEvent = UserInterest(event.id, userId)
            if (userAndInterestMapToAnEvent.get(event.id) == 0) {
                addInterestForTheUser(userIntererstForEvent, holder, event.id)
                holder.itemView.coming_question.setImageResource(R.drawable.drive_eta_24px)
                holder.itemView.interest_label.text = "Going"
                holder.itemView.interest_label.setTextColor(Color.rgb(30, 118, 0))
                var interestedPeopleCount = holder.itemView.people_coming.text.toString().toInt()
                interestedPeopleCount = interestedPeopleCount + 1
                holder.itemView.people_coming.text = interestedPeopleCount.toString()
                userAndInterestMapToAnEvent.put(event.id, 1)
            } else {
                deleteInterestForTheSUer(userId, event.id, 1, holder)
                holder.itemView.coming_question.setImageResource(R.drawable.coming_question)
                holder.itemView.interest_label.text = "Coming?"
                holder.itemView.interest_label.setTextColor(Color.rgb(37, 183, 211))
                var interestedPeopleCount: Int =
                    holder.itemView.people_coming.text.toString().toInt()
                interestedPeopleCount = interestedPeopleCount - 1
                holder.itemView.people_coming.text = interestedPeopleCount.toString()
                userAndInterestMapToAnEvent.put(event.id, 0)
            }

        }

        holder.itemView.linear_share_id.setOnClickListener {
            context.startActivity(getShareIntentWithMsg(event))
        }

        //resolve if there is external activity (from package manager) with intent filter of plain text type)
        if (null == getShareIntentWithMsg(event).resolveActivity(activity!!.packageManager)) {
            holder.itemView.share_event_icon.visibility = View.GONE
            holder.itemView.share_label.visibility = View.GONE
        } else {
            holder.itemView.share_event_icon.visibility = View.VISIBLE
            holder.itemView.share_label.visibility = View.VISIBLE
        }

        holder.bind(event)
        getInterestsForEachEvent(userId, event.id, holder)

    }

    private fun getShareIntentWithMsg(event: Event): Intent {
        val message = StringBuilder()
        message.append(context.getString(R.string.share_slogan))
            .append("\n")
            .append(context.getString(R.string.share_app_title))
            .append("\n")
            .append("Event")
            .append(": ")
            .append(event.title)
            .append("\n")
            .append("@: ")
            .append(event.eventLocation)
            .append("\n")
            .append("City: ")
            .append(event.city)
            .append("\n")
            .append("Country: ")
            .append(event.country)
            .append("\n")
            .append("Date: ")
            .append(event.eventDate)
            .append("\n")
            .append("Time: ")
            .append(event.eventTime)
            .append("\n")
            .append(event.eventimage.url)
            .append("\n")
            .append("Detail: ")
            .append(event.description)
        return ShareCompat.IntentBuilder.from(activity)
            .setText(message.toString())
            .setType("text/plain")
            .intent

    }

    val coroutinJobForGetingIntersts = Job()
    val coroutineScopeForGetingInterests =
        CoroutineScope(coroutinJobForGetingIntersts + Dispatchers.Main)

    private fun getInterestsForEachEvent(
        userId: Int,
        eventId: Int,
        holder: EventViewHolder
    ) {
        coroutineScopeForGetingInterests.launch {
            val interestGetterTask = InterestApi.retrofitService.getEventInterests(userId, eventId)
            try {

                val interestGetterResponse = interestGetterTask.await()
                holder.itemView.people_coming.text = interestGetterResponse.count.toString()
                userAndInterestMapToAnEvent.put(
                    eventId,
                    interestGetterResponse.user
                ) //event id to retrieved userId (1 interested, 0 not)
                if (interestGetterResponse != null) {
                    holder.itemView.people_coming.text = interestGetterResponse.count.toString()
                    if (interestGetterResponse.user > 0) {
                        holder.itemView.coming_question.setImageResource(R.drawable.drive_eta_24px)
                        holder.itemView.interest_label.text = "Going"
                        holder.itemView.interest_label.setTextColor(Color.rgb(30, 118, 0))
                    } else {
                        holder.itemView.interest_label.text = "Coming?"
                        holder.itemView.interest_label.setTextColor(Color.rgb(37, 183, 211))
                        holder.itemView.coming_question.setImageResource(R.drawable.coming_question)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //==========================DELETE USER INTEREST=========================
    val coroutinJobForDeletingInterest = Job()
    val coroutineScopeForDeletingInterest =
        CoroutineScope(coroutinJobForDeletingInterest + Dispatchers.Main)

    private fun deleteInterestForTheSUer(
        userId: Int,
        eventId: Int,
        deleteFlag: Int,
        holder: EventViewHolder
    ) {

        coroutineScopeForDeletingInterest.launch {
            val interestDeleteTask =
                InterestApi.retrofitService.deleteUserInterstAndGet(userId, eventId, deleteFlag)
            try {
                val interestDeleteAndGetResponse = interestDeleteTask.await()
                // Subtract number of people coming by one
                /*if (interestDeleteTask.isCompleted){

                }*/
                Toast.makeText(context, "Interst Deleted!!", Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //==========================CREATE NEW INTEREST=========================
    val coroutinJobForInterest = Job()
    val coroutineScopeForInterest = CoroutineScope(coroutinJobForInterest + Dispatchers.Main)
    private fun addInterestForTheUser(
        userIntererstForEvent: UserInterest,
        holder: EventViewHolder,
        eventId: Int
    ) {

        coroutineScopeForInterest.launch {
            val interestCreationTask =
                InterestApi.retrofitService.postUserIneterst(userIntererstForEvent)
            try {

                val interestResponse = interestCreationTask.await()
                /*if (interestCreationTask.isCompleted){

                }*/

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //=========================================================================
    private fun moreOptionPopupMenu(
        userId: Int,
        event: Event,
        holder: EventViewHolder,
        position: Int
    ) {
        if (userId == event.userId) {
            //Set the visibility VISIBLE if the event belongs to the signed in user
            holder.itemView.item_more_option.visibility = View.VISIBLE
        } else {
            holder.itemView.item_more_option.visibility = View.GONE
        }
        holder.itemView.item_more_option.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.itemView.item_more_option)
            popupMenu.inflate(R.menu.more_option_menu)
            if (userId == event.userId) {
                popupMenu.show()
            }
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete_event -> deleteEventFromServer(
                        holder.itemView.context,
                        event,
                        position
                    )
                    R.id.status_change -> changeEventStatus(
                        holder.itemView.context,
                        event,
                        position
                    )
                    else -> {
                        updateEventFromServer(event)
                    }
                }
            }

        }
    }

    private fun changeEventStatus(context: Context?, event: Event, position: Int): Boolean {

        val layoutInflater: LayoutInflater =
            context?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = layoutInflater.inflate(R.layout.change_status_popup_window, null)
        val popupWindow: PopupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isTouchable = true
        popupWindow.isFocusable = true

        if (Build.VERSION.SDK_INT >= 21) {
            popupWindow.elevation = 5.0f
        }

        val point = Point()
        val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            windowManager.defaultDisplay.getRealSize(point)
        else
            windowManager.defaultDisplay.getSize(point)
        val screenWidth = point.x
        val screenHeight = point.y

        popupWindow.width = screenWidth / 2 + screenWidth / 3
        popupWindow.height = screenHeight / 5

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        val spinner: Spinner = popupView.change_event_status_spinner
        ArrayAdapter.createFromResource(
            context, R.array.change_event_status_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }


        popupView.save_change_button.setOnClickListener {

            var statusValue = spinner.selectedItem.toString()
            statusValue = statusValue.toLowerCase()
            if (statusValue.equals("close")) {
                statusValue = "closed"
            }
            changeEventStatusOnBackground(statusValue, event, position)
            Toast.makeText(context, statusValue, Toast.LENGTH_LONG).show()
            popupWindow.dismiss()
        }
        return false
    }

    private fun changeEventStatusOnBackground(
        statusValue: String,
        event: Event,
        position: Int
    ) {

        coroutineScopeForDeletingInterest.launch {
            // Get the Deferred object for our Retrofit request
            val status = RequestBody.create(MediaType.parse("multipart/form-data"), statusValue)
            var getPropertiesDeferred =
                EventApi.retrofitService.updateEventStatusForEvent(event.id, status)
            try {
                val listResult = getPropertiesDeferred.await()
                val responseList = eventListViewModel.eventResponses.value as ArrayList
                if (statusValue.equals("closed")) {
                    responseList.remove(event)
                    notifyItemRemoved(position)
                    notifyItemMoved(position, 1)
                    notifyDataSetChanged()
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun updateEventFromServer(event: Event?): Boolean {


        val eventToBeUpdated = Bundle()
        eventToBeUpdated.putParcelable("EVENT", event)
        val fragmentManager = activity!!.supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        val newEventCreatingFragment: Fragment = NewEventCreatingFragment()
        newEventCreatingFragment.arguments = eventToBeUpdated
        fragmentTransaction.add(R.id.nav_host_fragment, newEventCreatingFragment)
            .addToBackStack(null)
        fragmentTransaction.commit()


        return false
    }

    val coroutinJob = Job()
    val coroutineScope = CoroutineScope(coroutinJob + Dispatchers.Main)

    private fun deleteEventFromServer(
        context: Context,
        event: Event,
        position: Int
    ): Boolean {

        var okDeleteRespose = false
        coroutineScope.launch {
            val deleteTask = EventApi.retrofitService.deleteEvent(event.id)
            try {
                val delRespose = deleteTask.await()
                if (delRespose != null) {
                    okDeleteRespose = true
                    val eventList = eventListViewModel.eventResponses.value!! as ArrayList
                    eventList.remove(event)
                    notifyItemRemoved(position)
                    notifyItemMoved(position, 1)
                    notifyDataSetChanged()
                    Toast.makeText(context, "EVENT DELETED SUCCESSFULLY!!!!!", Toast.LENGTH_LONG)
                        .show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "EVENT COULDN'T BE DELETED!!!!!", Toast.LENGTH_LONG).show()
                okDeleteRespose = false
            }
        }

        return okDeleteRespose
    }

    class EventViewHolder constructor(private var binding: EventsListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {

            binding.eventsData = event
            binding.executePendingBindings()
        }
    }

    class OnclickListner(val clickListner: (event: Event) -> Unit) {
        fun onClick(event: Event) = clickListner(event)
    }

}
