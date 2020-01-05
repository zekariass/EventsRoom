package com.mobapproject.eventsroom.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.mobapproject.eventsroom.databinding.FragmentEventDetailBinding
import com.mobapproject.eventsroom.modelviews.EventDetailViewModel
import com.mobapproject.eventsroom.modelviews.EventDetailViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class EventDetailFragment : Fragment() {

    private lateinit var binding: FragmentEventDetailBinding;
    /* private val viewModel : EventDetailViewModel by lazy {
         ViewModelProviders.of(this).get(EventDetailViewModel::class.java)
     }*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_event_detail, container, false)

        val application = requireNotNull(activity).application
        binding = FragmentEventDetailBinding.inflate(inflater)
        binding.setLifecycleOwner(this)
        val event = EventDetailFragmentArgs.fromBundle(arguments!!).selectedEvent
        val viewModelFactory = EventDetailViewModelFactory(event, application)
        binding.viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(EventDetailViewModel::class.java)

        val showCurrentCityWeatherTextView = binding.showCurrentWeather
        val currentCity = event.city
        showCurrentCityWeatherTextView.setOnClickListener {
            this.findNavController().navigate(
                EventDetailFragmentDirections.actionEventDetailFragmentToCurrentWeatherFragment(
                    currentCity.toString()
                )
            )
        }

        return binding.root
    }


}
