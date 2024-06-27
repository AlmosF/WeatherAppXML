package com.example.hw1.details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import com.example.hw1.R
import com.example.hw1.databinding.FragmentDetailsMoreBinding
import java.text.SimpleDateFormat

class DetailsMoreFragment : Fragment() {


    companion object{
        const val sunrise = "sunrise"
        const val sunset = "sunset"
        const val humidity = "humidity"
        const val windS = "windS"
        const val windD = "windD"
        const val groundLevel = "groundLevel"
        const val seaLevel = "seaLevel"
        const val pressure = "pressure"
        const val clouds = "clouds"
        const val sdf = "sdf"
    }

    private lateinit var binding : FragmentDetailsMoreBinding
    private var weatherDataHolder : WeatherDataHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsMoreBinding.inflate(LayoutInflater.from(context))
        loadValues()

        return binding.root
    }



    private fun loadValues() {
        val sunrise = activity?.intent?.extras?.getLong(sunrise)
        val sunset = activity?.intent?.extras?.getLong(sunset)
        if(activity?.intent?.extras?.getSerializable(sdf) != null){
            var sdf = activity?.intent?.extras?.getSerializable(sdf) as SimpleDateFormat
            var localSunrise = sdf.format(sunrise!!*1000)
            var localSunset = sdf.format(sunset!!*1000)
            binding.sunRiseTime.text = localSunrise.toString()
            binding.sunSetTime.text = localSunset.toString()
        }else{
            binding.sunRiseTime.text = "ERROR"
            binding.sunSetTime.text = "ERROR"
        }

        binding.humidity.text = String.format(getString(R.string.humidity), activity?.intent?.extras?.getDouble(humidity))
        var isMetric = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString("unit_preference_title", "Metric") == "Metric"
        when(isMetric){
            true -> {
                binding.wind.text = String.format(getString(R.string.windspeedM), activity?.intent?.extras?.getDouble(windS))
            }
            false ->{
                binding.wind.text = String.format(getString(R.string.windSpeedI), activity?.intent?.extras?.getDouble(windS))
            }
        }
        binding.windD.text = String.format(getString(R.string.windDeg, activity?.intent?.extras?.getDouble(windD)))
        binding.groundLevel.text = String.format(getString(R.string.pressure), activity?.intent?.extras?.getDouble(groundLevel))

        binding.seaLevel.text = String.format(getString(R.string.pressure), activity?.intent?.extras?.getDouble(seaLevel))

        binding.pressure.text = String.format(getString(R.string.pressure), activity?.intent?.extras?.getDouble(pressure))

        binding.cloud.text = String.format(getString(R.string.cloudiness), activity?.intent?.extras?.getInt(clouds))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val animationPreference = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("animation_preference_title", "Off") == "On"
        if(animationPreference){
            binding.sunSet.startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_top))
            binding.SunRise.startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_bottom))
        }
    }
}