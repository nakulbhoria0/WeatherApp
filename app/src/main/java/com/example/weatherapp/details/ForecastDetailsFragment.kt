package com.example.weatherapp.details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.weatherapp.*

class ForecastDetailsFragment : Fragment() {

    private val args: ForecastDetailsFragmentArgs by navArgs()
    private lateinit var tempDisplaySettingManager: TempDisplaySettingManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        tempDisplaySettingManager = TempDisplaySettingManager(context = requireContext())


        val view = inflater.inflate(R.layout.fragment_forecast_details, container, false)

        val tempTextView = view.findViewById<TextView>(R.id.tempTextView)
        val descriptionText = view.findViewById<TextView>(R.id.descriptionTextView)


        val temp = args.temp
        tempTextView.text = formatTempForDisplay(temp, tempDisplaySettingManager.getTempDisplaySetting())
        descriptionText.text = args.description

        return view
    }


}