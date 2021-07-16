package com.example.weatherapp.forecast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.*
import com.example.weatherapp.api.DailyForecast
import com.example.weatherapp.api.WeeklyForecast
import com.google.android.material.floatingactionbutton.FloatingActionButton

class WeeklyForecastFragment : Fragment() {


    private val forecastRepository = ForecastRepository() // repository
    private lateinit var locationRepository: LocationRepository // locationRepository
    private lateinit var tempDisplaySettingManager: TempDisplaySettingManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        tempDisplaySettingManager = TempDisplaySettingManager(requireContext())

        // getting zipcode from bundle
        val zipcode = arguments?.getString(KEY_ZIPCODE) ?: ""

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_weekly_forecast, container, false)

        // fab button
        val locationEntryButton: FloatingActionButton = view.findViewById(R.id.locationEntryButton)

        // fab onClick
        locationEntryButton.setOnClickListener {
            showLocationEntry()
        }

        // forecastList is our RecyclerView
        val forecastList: RecyclerView = view.findViewById(R.id.forecastRecyclerView)

        // setting LinearLayoutManager to RecyclerView.layoutManager
        forecastList.layoutManager = LinearLayoutManager(requireContext())

        // getting instance of DailyForecastAdapter
        // a function to handle item click
        val dailyForecastAdapter = DailyForecastAdapter(tempDisplaySettingManager) { forecast ->

            // create and send intent to ForecastDetailsActivity
            showForecastDetails(forecast)
        }

        // setting that adapter to our RecyclerView
        forecastList.adapter = dailyForecastAdapter

        // observer for DailyForecast List LiveData
        val weeklyForecastObserver = Observer<WeeklyForecast> { weeklyForecast ->
            // update our list adapter
            dailyForecastAdapter.submitList(weeklyForecast.daily)
        }

        // observing weekly forecast from repository
        forecastRepository.weeklyForecast.observe(viewLifecycleOwner, weeklyForecastObserver)

        // loading temp Data
        locationRepository = LocationRepository(requireContext())
        val savedLocationObserver = Observer<Location>{savedLocation ->
            when(savedLocation){
                is Location.Zipcode -> forecastRepository.loadWeeklyForecast(savedLocation.zipcode)
            }
        }
        locationRepository.savedLocation.observe(viewLifecycleOwner, savedLocationObserver)


        return view

    }

    private fun showLocationEntry() {
        val action = WeeklyForecastFragmentDirections.actionWeeklyForecastFragmentToLocationEntryFragment()
        findNavController().navigate(action)
    }

    /**
     * this function creates and start intent with details we want to display
     * to ForecastDetailsActivity
     * @param forecast is dta we want to send to ForecastDetailsActivity
     * and display
     */
    private fun showForecastDetails(forecast: DailyForecast) {
        val temp = forecast.temp.max
        val description = forecast.weather[0].description
        val action = WeeklyForecastFragmentDirections
            .actionWeeklyForecastFragmentToForecastDetailsFragment(
                temp, description
            )
        findNavController().navigate(action)
    }


    companion object{
        const val KEY_ZIPCODE = "key_zipcode"

        fun newInstance(zipcode: String): WeeklyForecastFragment{
            val fragment = WeeklyForecastFragment()
            val args = Bundle()
            args.putString(KEY_ZIPCODE, zipcode)
            fragment.arguments = args
            return fragment
        }
    }



}