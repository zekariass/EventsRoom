package com.mobapproject.eventsroom.fragments


import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.mobapproject.eventsroom.R
import com.mobapproject.eventsroom.adapters.EventsRecyclerAdapter
import com.mobapproject.eventsroom.data.User
import com.mobapproject.eventsroom.databinding.FragmentEventListBinding
import com.mobapproject.eventsroom.decorations.CardViewDecoration
import com.mobapproject.eventsroom.helpers.CurrentLocationProvider
import com.mobapproject.eventsroom.helpers.UserUtils
import com.mobapproject.eventsroom.modelviews.EventListViewModel
import com.mobapproject.eventsroom.modelviews.EventListViewModelFactory

/**
 * A simple [Fragment] subclass.
 *
 */


class EventListFragment : Fragment() {

    lateinit var binding: FragmentEventListBinding
    private val RC_LOG_IN = 111
    private lateinit var providers: ArrayList<AuthUI.IdpConfig>
    private val TAG = "SIGNIN ERROR"
    private lateinit var userPref: SharedPreferences
    private lateinit var viewModel: EventListViewModel
    private lateinit var eventAdapter: EventsRecyclerAdapter

    var currCity = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(activity).application
        binding = FragmentEventListBinding.inflate(inflater)
        binding.setLifecycleOwner(this)
        userPref = PreferenceManager.getDefaultSharedPreferences(context)


        val currentLocationProvider = CurrentLocationProvider()
        currCity = currentLocationProvider.getCurrentLocation(context!!, this)

        val modelFactory = EventListViewModelFactory(application)
        viewModel = ViewModelProviders.of(this, modelFactory).get(EventListViewModel::class.java)
        binding.viewModel = viewModel

        initializeRecyclerView()

        setHasOptionsMenu(true)

        viewModel.navigateToEventDetailFragmetValue.observe(this, Observer {
            if (null != it) {
                this.findNavController().navigate(
                    EventListFragmentDirections.actionEventListFragmentToEventDetailFragment(it)
                )
                viewModel.displayEvenetDetailFragmetComplete()
            }
        })

        providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
        )

        if (FirebaseAuth.getInstance().currentUser == null) {
            showTheSignInOptions()
        }

        return binding.root
    }


    private fun showTheSignInOptions() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.mipmap.signin_logo)
                .build(), RC_LOG_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_LOG_IN) {
            val myResponse = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser

                val loggedUser = User(user?.displayName!!, user?.uid, user?.email!!)
                UserUtils.getUserInfo(context!!, loggedUser)

                // Refresh the fragment when new user logged in to uspdate list of events
                refreshEventListFragment()
                //viewModel.retrieveEventsInBackground(currCity)

            } else {
                activity!!.finish()
                Log.i(TAG, myResponse?.error?.errorCode.toString())
            }
        }
    }

    private fun refreshEventListFragment() {
        val ft = fragmentManager!!.beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false)
        }
        ft.detach(this).attach(this).commit()
    }

    private fun initializeRecyclerView() {
        binding.eventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@EventListFragment.context)
            val cardDecoration = CardViewDecoration(40)
            addItemDecoration(cardDecoration)
            eventAdapter = EventsRecyclerAdapter(EventsRecyclerAdapter.OnclickListner {
                viewModel.displayEvenetDetailFragmet(it)
            }, this@EventListFragment, context, activity)
            adapter = eventAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.event_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.newEventCreatingFragment -> return NavigationUI.onNavDestinationSelected(
                item!!,
                view!!.findNavController()
            )
            R.id.filter_by_city_and_country -> viewModel.leadEventsByCityAndCountry(context)
            R.id.get_all_events -> viewModel.retrieveEventsInBackground(currCity)
            R.id.refresh -> refreshEventListFragment()
            R.id.show_my_events -> viewModel.getMyEvents(context)
            R.id.about -> viewModel.showAboutPopupWindow(context)
            R.id.Signout -> {
                AuthUI.getInstance()
                    .signOut(context!!)
                    .addOnCompleteListener {
                        Log.i("SIGNOUT: ", "SIGNED OUT!!!")
                        showTheSignInOptions()
                    }
            }
        }

        super.onOptionsItemSelected(item)
        return true

    }

}
