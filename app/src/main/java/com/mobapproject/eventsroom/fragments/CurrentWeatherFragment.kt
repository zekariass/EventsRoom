package com.mobapproject.eventsroom.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.mobapproject.eventsroom.databinding.FragmentCurrentWeatherBinding
import com.mobapproject.eventsroom.modelviews.CurrentWeatherViewModelFactory
import com.mobapproject.eventsroom.modelviews.CurrentWetherViewModel

class CurrentWeatherFragment : Fragment() {


    private lateinit var binding: FragmentCurrentWeatherBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentCurrentWeatherBinding.inflate(inflater)
        val currentCityFromDetailFragment =
            CurrentWeatherFragmentArgs.fromBundle(arguments!!).cityOfEvent
        val viewModelFactory = CurrentWeatherViewModelFactory(currentCityFromDetailFragment)
        val viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(CurrentWetherViewModel::class.java)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        //viewModel.getWeatherForCurrentCity()
        return binding.root
    }

}
