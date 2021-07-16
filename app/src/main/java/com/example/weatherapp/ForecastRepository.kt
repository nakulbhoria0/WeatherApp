package com.example.weatherapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.api.CurrentWeather
import com.example.weatherapp.api.WeeklyForecast
import com.example.weatherapp.api.createOpenWeatherMapService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.random.Random

class ForecastRepository {

    // private _currentForecast to update weather data inside repository only
    private val _currentWeather = MutableLiveData<CurrentWeather>()

    // public currentForecast to observe data
    // Immutable so other activities can't change the value
    val currentWeather:LiveData<CurrentWeather> = _currentWeather

    // private weeklyForecast to update weather data inside repository only
    private val _weeklyForecast = MutableLiveData<WeeklyForecast>()

    // public weeklyForecast to observe data
    // Immutable so other activities can't change the value
    val weeklyForecast: LiveData<WeeklyForecast> = _weeklyForecast

    /**
     * function for load weekly forecast and update our _weeklyForecast data
     * @param zipcode is zipcode we got from the user
     */
    fun loadWeeklyForecast(zipcode: String){
        val call = createOpenWeatherMapService().currentWeather(fullZipcode(zipcode), "metric", BuildConfig.OPEN_WEATHER_MAP_API_KEY)
        call.enqueue(object : Callback<CurrentWeather> {
            override fun onResponse(
                call: Call<CurrentWeather>,
                response: Response<CurrentWeather>
            ) {
                val weatherResponse = response.body()
                if (weatherResponse != null) {
                    // 7 day forecast
                    val call = createOpenWeatherMapService().sevenDayForecast(
                        lat =weatherResponse.coord.lat,
                        lon =  weatherResponse.coord.lon,
                        exclude = "current,minutely,hourly",
                        units = "metric",
                        apiKey = BuildConfig.OPEN_WEATHER_MAP_API_KEY
                    )
                    call.enqueue(object : Callback<WeeklyForecast> {
                        override fun onResponse(
                            call: Call<WeeklyForecast>,
                            response: Response<WeeklyForecast>
                        ) {
                            val weeklyForecastResponse = response.body()
                            if (weeklyForecastResponse != null) {
                                _weeklyForecast.value = weeklyForecastResponse
                            }
                        }

                        override fun onFailure(call: Call<WeeklyForecast>, t: Throwable) {
                            Log.e("ForecastRepository", "Error Loading weekly forecast", t)
                        }

                    })
                }
            }

            override fun onFailure(call: Call<CurrentWeather>, t: Throwable) {
                Log.e("ForecastRepository", "Error Loading location for weekly forecast", t)
            }

        })
    }

    fun loadCurrentForecast(zipcode: String){



        val call = createOpenWeatherMapService().currentWeather(fullZipcode(zipcode), "metric", BuildConfig.OPEN_WEATHER_MAP_API_KEY)
        call.enqueue(object : Callback<CurrentWeather> {
            override fun onResponse(
                call: Call<CurrentWeather>,
                response: Response<CurrentWeather>
            ) {
                val weatherResponse = response.body()
                if (weatherResponse != null) {
                    _currentWeather.value = weatherResponse
                }
            }

            override fun onFailure(call: Call<CurrentWeather>, t: Throwable) {
                Log.e("ForecastRepository", "Error Loading Current Weather", t)
            }

        })
    }

    private fun getTempDescription(temp: Float): String {
        return when (temp) {
            in Float.MIN_VALUE.rangeTo(0f) -> "Anything below 0 doesn't make any sense"
            in 0f.rangeTo(32f) -> "Way too cold"
            in 32f.rangeTo(55f) -> "Colder than I would prefer"
            in 55f.rangeTo(65f) -> "Getting better"
            in 65f.rangeTo(80f) -> "That's the sweet spot!"
            in 80f.rangeTo(90f) -> "Getting a little warm"
            in 90f.rangeTo(100f) -> "Where's the A/C?"
            in 100f.rangeTo(Float.MAX_VALUE) -> "What is this, Arizona?"
            else -> "Does not compute"
        }

    }

    /**
     * by default API uses USA zipcode
     * we have to specify that we want to use Indian Zipcode
     * ,IN is keyword to use Indian Zipcode
     * @param zipcode is zipcode we got from the user
     */
    private fun fullZipcode(zipcode: String) = "$zipcode,IN"
}