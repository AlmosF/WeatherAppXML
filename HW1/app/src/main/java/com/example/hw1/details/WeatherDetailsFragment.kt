package com.example.hw1.details

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.Color.GRAY
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.hw1.R
import com.example.hw1.databinding.ActivityMainBinding
import com.example.hw1.databinding.FragmentDetailsMainBinding
import com.example.hw1.databinding.FragmentHomeBinding
import com.example.hw1.databinding.FragmentWeatherDetailsBinding
import com.example.hw1.model.WeatherData
import com.example.hw1.network.NetworkManager
import com.example.hw1.network.WeatherAPI
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class WeatherDetailsFragment : Fragment(), WeatherDataHolder {

    private var weatherData: WeatherData? = null
    lateinit var binding: FragmentWeatherDetailsBinding
    lateinit var homeFragment : FragmentHomeBinding
    lateinit var weatherAPI : WeatherAPI
    lateinit var main : String
    lateinit var timeinhours : String

    companion object{
        private const val TAG = "WeatherDetailsFragment"
        const val EXTRA_CITY_NAME = "extra.city_name"
        const val CURRENT_LOCATION = "####"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeFragment = FragmentHomeBinding.inflate(LayoutInflater.from(context))
        binding = FragmentWeatherDetailsBinding.inflate(LayoutInflater.from(context))
        binding.fwdtvCity.text = activity?.intent?.extras?.getString(EXTRA_CITY_NAME)
        //Log.w("Extra", binding.fwdtvCity.text.toString())

        //Log.w("city", binding.fwdtvCity.text.toString())
        //Log.w("Current", activity?.intent?.extras?.getString(CURRENT_LOCATION).toString())
        if(binding.fwdtvCity.text == activity?.intent?.extras?.getString(CURRENT_LOCATION)){
            binding.locationIcon.visibility = View.VISIBLE
        }

        var showDetailsIntent = activity?.intent
        showDetailsIntent?.putExtra(DetailsMainFragment.city, binding.fwdtvCity.text)

        val detailsPagerAdapter = DetailsPagerAdapter(requireActivity())
        binding.mainViewPager.adapter = detailsPagerAdapter

        initializeValues()


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        TabLayoutMediator(binding.tabLayout, binding.mainViewPager){
            tab, position ->
            tab.text = when(position){
                0 -> "Main"
                1 -> "Details"
                else -> ""
            }
        }.attach()
        initializeValues()
    }

    private fun initializeValues() {
        //getWeatherData()
        //binding.root.rootView.background = resources.getDrawable(R.drawable.rain)
        loadWeatherData(){
            setBG(main)
        }
        //Custom BG if weather
    }

    private fun setBG(main: String){
        var hour = timeinhours.toInt()
        //Log.w("hour", hour.toString())
        when(main){
            "Clear" -> {
                if(hour in 6..18)
                    binding.Layout.background = resources.getDrawable(R.drawable.clear)
                else
                    binding.Layout.background = resources.getDrawable(R.drawable.clearnight)
            }
            "Thunderstorm" -> {
                if(hour in 6..18)
                    binding.Layout.background = resources.getDrawable(R.drawable.thunderstorm)
                else
                    binding.Layout.background = resources.getDrawable(R.drawable.thunderstormnight)
            }
            "Rain" -> {
                if (hour in 6..18)
                    binding.Layout.background = resources.getDrawable(R.drawable.rain)
                else
                    binding.Layout.background = resources.getDrawable(R.drawable.rainnight)
            }
            "Clouds" -> {
                if(hour in 6..18)
                    binding.Layout.background = resources.getDrawable(R.drawable.clouds)
                else
                    binding.Layout.background = resources.getDrawable(R.drawable.smokenight)
            }
            "Mist" -> {
                if (hour in 6..18)
                    binding.Layout.background = resources.getDrawable(R.drawable.mist)
                else
                    binding.Layout.background = resources.getDrawable(R.drawable.mistnight)
            }
            "Tornado" -> {
                if(hour in 6..18)
                    binding.Layout.background = resources.getDrawable(R.drawable.tornado)
                else
                    binding.Layout.background = resources.getDrawable(R.drawable.tornadonight)
            }
            "Snow" -> {
                if(hour in 6..18)
                    binding.Layout.background = resources.getDrawable(R.drawable.snow)
                else
                    binding.Layout.background = resources.getDrawable(R.drawable.snownight)
            }
            "Drizzle" -> {
                if(hour in 6..18)
                    binding.Layout.background = resources.getDrawable(R.drawable.drizzle)
                else
                    binding.Layout.background = resources.getDrawable(R.drawable.drizzlenight)
            }
            "Smoke" -> {
                if (hour in 6..18)
                    binding.Layout.background = resources.getDrawable(R.drawable.smoke)
                else
                    binding.Layout.background = resources.getDrawable(R.drawable.smokenight)
            }
            "Haze" -> {
                if(hour in 6..18)
                    binding.Layout.background = resources.getDrawable(R.drawable.haze)
                else
                    binding.Layout.background = resources.getDrawable(R.drawable.hazenight)
            }
            "Fog" -> {
                binding.Layout.background = resources.getDrawable(R.drawable.fog)
            }
            "Squall" -> {
                binding.Layout.background = resources.getDrawable(R.drawable.squall)
            }
            /*"Dust" -> { //Ezekhez nem találtam megfelelő képet, nem is értem mik ezek az időjárásokxD
                binding.root.rootView.background = resources.getDrawable(R.drawable.dust)
            }
            "Sand" -> {
                binding.root.rootView.background = resources.getDrawable(R.drawable.sand)
            }
            "Ash" -> {
                binding.root.rootView.background = resources.getDrawable(R.drawable.ash)
            }*/
            else -> {
                if (hour in 6..18)
                    binding.Layout.background = resources.getDrawable(R.drawable.clear)
                else
                    binding.Layout.background = resources.getDrawable(R.drawable.clearnight)
            }
        }
    }




    private fun loadWeatherData(callback: () -> Unit) {
        NetworkManager.getWeather(binding.fwdtvCity.text.toString(), requireContext())?.enqueue(object : Callback<WeatherData?> {
            override fun onResponse(
                call: Call<WeatherData?>,
                response: Response<WeatherData?>
            ) {
                Log.d(TAG, "onResponse: " + response.code())
                val isMetric = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("unit_preference_title", "Metric") == "Metric"
                if (response.isSuccessful) {

                    val showDetailsIntent = activity?.intent
                    showDetailsIntent?.setClass(requireContext(), DetailsMoreFragment::class.java)
                    showDetailsIntent?.putExtra(DetailsMoreFragment.sunrise, response.body()?.sys?.sunrise)
                    showDetailsIntent?.putExtra(DetailsMoreFragment.sunset, response.body()?.sys?.sunset)
                    showDetailsIntent?.putExtra(DetailsMoreFragment.humidity, response.body()?.main?.humidity?.toDouble())
                    showDetailsIntent?.putExtra(DetailsMoreFragment.windS, response.body()?.wind?.speed?.toDouble())
                    showDetailsIntent?.putExtra(DetailsMoreFragment.windD, response.body()?.wind?.deg?.toDouble())
                    showDetailsIntent?.putExtra(DetailsMoreFragment.groundLevel, response.body()?.main?.grnd_level?.toDouble())
                    showDetailsIntent?.putExtra(DetailsMoreFragment.seaLevel, response.body()?.main?.sea_level?.toDouble())
                    showDetailsIntent?.putExtra(DetailsMoreFragment.pressure, response.body()?.main?.pressure?.toDouble())
                    showDetailsIntent?.putExtra(DetailsMainFragment.main, response.body()?.weather?.get(0)?.main)
                    showDetailsIntent?.putExtra(DetailsMainFragment.description, response.body()?.weather?.get(0)?.description)
                    showDetailsIntent?.putExtra(DetailsMainFragment.clouds, response.body()?.clouds?.all)
                    showDetailsIntent?.putExtra(DetailsMoreFragment.clouds, response.body()?.clouds?.all)

                    main = response.body()?.weather?.get(0)?.main.toString()
                    //Log.w("mainSI", main)

                    //showDetailsIntent?.putExtra(DetailsMainFragment.city, binding.fwdtvCity.text)

                    when(isMetric){
                        true -> {//Felesleges modosítani, mert pontatlanabb
                            //binding.fwdtvCity.text = response.body()?.name
                            binding.fwdtvTemp.text = String.format(
                                getString(R.string.celsius),
                                response.body()?.main?.temp
                            )
                            binding.MaxMinFeelsLikeTemp.text =
                                String.format(getString(R.string.maxminfeelslikeC, response.body()?.main?.temp_max,
                                    response.body()?.main?.temp_min, response.body()?.main?.feels_like))
                        }
                        false -> {//Felesleges modosítani, mert pontatlanabb
                            //binding.fwdtvCity.text = response.body()?.name
                            binding.fwdtvTemp.text = String.format(getString(R.string.farenheit), response.body()?.main?.temp)
                            binding.MaxMinFeelsLikeTemp.text =
                                String.format(getString(R.string.maxminfeelslikeF, response.body()?.main?.temp_max,
                                    response.body()?.main?.temp_min, response.body()?.main?.feels_like))
                        }
                    }

                    var date = Date(System.currentTimeMillis())
                    //Log.w("date", date.toString())
                    var sign = if(response.body()?.timezone?.toLong()!! >= 0) "+" else "-"
                    var absTimezone = Math.abs(response.body()?.timezone?.toLong()!! / 3600)
                    val timeZone = TimeZone.getTimeZone("GMT$sign$absTimezone")


                    var sdf = SimpleDateFormat("E, HH:mm")
                    sdf.timeZone = timeZone
                    showDetailsIntent?.putExtra(DetailsMoreFragment.sdf, sdf)
                    showDetailsIntent?.putExtra(DetailsMainFragment.sdf, sdf)
                    var localTime = sdf.format(date)
                    //substring after ", " and before ":" to get the hours
                    timeinhours = localTime.substringAfter(", ").substringBefore(":")

                    //Log.w("localtime", localTime)
                    binding.dayTime.text = localTime

                    Glide.with(context!!)
                        .load("https://openweathermap.org/img/w/${response.body()?.weather?.get(0)?.icon}.png")
                        .transition(DrawableTransitionOptions().crossFade())
                        .into(binding.fwdivIcon)


                    val detailsPagerAdapter = DetailsPagerAdapter(requireActivity())
                    binding.mainViewPager.adapter = detailsPagerAdapter
                    callback()
                } else {
                    Log.e("ERRORRRRRRRRRRRRRRRR", "ERRORRRRRRRRRRRRRRRR")
                    Toast.makeText(context, "Error: " + response.code(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<WeatherData?>,
                throwable: Throwable
            ) {
                throwable.printStackTrace()
                Toast.makeText(context, "Error: " + throwable.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun getWeatherData(): WeatherData? {
        return weatherData
    }

}