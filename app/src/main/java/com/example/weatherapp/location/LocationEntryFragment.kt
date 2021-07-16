package com.example.weatherapp.location

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.Location
import com.example.weatherapp.LocationRepository
import com.example.weatherapp.R


class LocationEntryFragment : Fragment() {

    private lateinit var locationRepository: LocationRepository


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        locationRepository = LocationRepository(requireContext())

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_location_entry, container, false)

        val zipcodeEditText: EditText = view.findViewById(R.id.zipcodeEditText) // zipcode EditText
        val submitButton: Button = view.findViewById(R.id.submitButton) // submit Button


        // set onClickListener to submitButton
        submitButton.setOnClickListener {
            // get the zipcode from zipcodeEditText
            val zipcode = zipcodeEditText.text.toString()

            // call submitOnClick method
            submitOnClick(zipcode)
        }

        return view

    }

    // onClickListener for submit Button
    private fun submitOnClick(zipcode: String) {
        // check zipcode is valid or not
        if (zipcode.length != 6) {
            // invalid zipcode
            Toast.makeText(requireContext(), R.string.enter_valid_string_text, Toast.LENGTH_SHORT)
                .show()
        } else {
            // valid zipcode  save zipcode to repository
                locationRepository.saveLocation(Location.Zipcode(zipcode))
                findNavController().navigateUp()
        }


    }

}