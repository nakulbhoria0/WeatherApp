package com.example.weatherapp.forecast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.*
import com.example.weatherapp.api.CurrentWeather
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CurrentForecastFragment : Fragment() {


    private val forecastRepository = ForecastRepository() // forecastRepository
    private lateinit var locationRepository: LocationRepository
    private lateinit var tempDisplaySettingManager: TempDisplaySettingManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        tempDisplaySettingManager = TempDisplaySettingManager(requireContext())

        // getting zipcode from bundle
        val zipcode = arguments?.getString(KEY_ZIPCODE) ?: ""

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_current_forecast, container, false)

        // location Name Text View
        val locationName:TextView = view.findViewById(R.id.locationName)

        // Temp Text View
        val tempText : TextView = view.findViewById(R.id.tempText)

        // fab button
        val locationEntryButton: FloatingActionButton = view.findViewById(R.id.locationEntryButton)

        // fab onClick
        locationEntryButton.setOnClickListener {
            showLocationEntry()
        }

        // LocationRepository
        locationRepository = LocationRepository(requireContext())

        // Observer for savedLocation LiveData from LocationRepository
        val savedLocationObserver = Observer<Location> { savedLocation ->
            when(savedLocation){
                is Location.Zipcode ->forecastRepository.loadCurrentForecast(savedLocation.zipcode)
            }
        }
        locationRepository.savedLocation.observe(viewLifecycleOwner, savedLocationObserver)



        // observer
        val currentWeatherObserver = Observer<CurrentWeather> { weather ->

            locationName.text = weather.name
            tempText.text = formatTempForDisplay(weather.forecast.temp, tempDisplaySettingManager.getTempDisplaySetting())

        }

        // observing weekly forecast from repository
        forecastRepository.currentWeather.observe(viewLifecycleOwner, currentWeatherObserver)



        return view

    }

    private fun showLocationEntry() {
        val action = CurrentForecastFragmentDirections.actionCurrentForecastFragmentToLocationEntryFragment()
        findNavController().navigate(action)
    }



    companion object{
        const val KEY_ZIPCODE = "key_zipcode"

        fun newInstance(zipcode: String): CurrentForecastFragment{
            val fragment = CurrentForecastFragment()
            val args = Bundle()
            args.putString(KEY_ZIPCODE, zipcode)
            fragment.arguments = args
            return fragment
        }
    }


}