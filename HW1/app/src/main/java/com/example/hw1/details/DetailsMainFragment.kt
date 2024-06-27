package com.example.hw1.details

import android.content.Context
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
import com.example.hw1.databinding.FragmentDetailsMainBinding
import com.example.hw1.model.WeatherData
import com.example.hw1.model.forecast.ForecastData
import com.example.hw1.model.forecast.WeatherForecast
import com.example.hw1.network.ForecastNertworkManager
import com.example.hw1.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class DetailsMainFragment : Fragment() {

    private lateinit var binding: FragmentDetailsMainBinding
    private var weatherDataHolder: WeatherDataHolder? = null

    var fragmentContext: Context? = null

    companion object{
        const val main = "main"
        const val description = "description"
        const val clouds = "clouds"
        const val city = "city"
        const val sdf = "sdf"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Log.w("Activity", activity.toString())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        binding = FragmentDetailsMainBinding.inflate(LayoutInflater.from(context))
        return binding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadValues()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
        loadForecastWeatherData()
    }


    private fun loadValues(){
        val main = activity?.intent?.extras?.getString(main)
        val description = activity?.intent?.extras?.getString(description)
        binding.tvMain.text = main.toString()
        binding.tvDescription.text = description.toString()
    }


    private fun loadForecastWeatherData() {


        var city = activity?.intent?.extras?.getString(city)
        //getstring-re problémás
        //Log.w("ForecasttCity", city.toString())
        ForecastNertworkManager.getWeather(city, requireContext())?.enqueue(object : Callback<ForecastData?> {
            override fun onResponse(
                call: Call<ForecastData?>,
                response: Response<ForecastData?>
            ) {
                val isMetric = PreferenceManager.getDefaultSharedPreferences(fragmentContext!!).getString("unit_preference_title", "Metric") == "Metric"
                if (response.isSuccessful) {


                    Glide.with(fragmentContext!!)
                        .load("https://openweathermap.org/img/w/${response.body()?.list?.get(0)?.weather?.get(0)?.icon}.png")
                        .transition(DrawableTransitionOptions().crossFade())
                        .into(binding.Forecast1st)

                    Glide.with(fragmentContext!!)
                        .load("https://openweathermap.org/img/w/${response.body()?.list?.get(1)?.weather?.get(0)?.icon}.png")
                        .transition(DrawableTransitionOptions().crossFade())
                        .into(binding.Forecast2nd)

                    Glide.with(fragmentContext!!)
                        .load("https://openweathermap.org/img/w/${response.body()?.list?.get(2)?.weather?.get(0)?.icon}.png")
                        .transition(DrawableTransitionOptions().crossFade())
                        .into(binding.Forecast3rd)

                    Glide.with(fragmentContext!!)
                        .load("https://openweathermap.org/img/w/${response.body()?.list?.get(3)?.weather?.get(0)?.icon}.png")
                        .transition(DrawableTransitionOptions().crossFade())
                        .into(binding.Forecast4th)

                    Glide.with(fragmentContext!!)
                        .load("https://openweathermap.org/img/w/${response.body()?.list?.get(4)?.weather?.get(0)?.icon}.png")
                        .transition(DrawableTransitionOptions().crossFade())
                        .into(binding.Forecast5th)

                    Glide.with(fragmentContext!!)
                        .load("https://openweathermap.org/img/w/${response.body()?.list?.get(5)?.weather?.get(0)?.icon}.png")
                        .transition(DrawableTransitionOptions().crossFade())
                        .into(binding.Forecast6th)

                    Glide.with(fragmentContext!!)
                        .load("https://openweathermap.org/img/w/${response.body()?.list?.get(6)?.weather?.get(0)?.icon}.png")
                        .transition(DrawableTransitionOptions().crossFade())
                        .into(binding.Forecast7th)




                    val temp_max = response.body()?.list?.get(0)?.main?.temp_max
                    val temp_min = response.body()?.list?.get(0)?.main?.temp_min
                    val feels_like = response.body()?.list?.get(0)?.main?.feels_like

                    val test : String = String.format(fragmentContext!!.getString(R.string.maxminfeelslikeF, temp_max, temp_min, feels_like))
                    //Log.w("Forecastt", test)

                    if(isMetric){
                        binding.tv1stForecastTemp.text = String.format(fragmentContext!!.getString(R.string.maxminC,
                            response.body()?.list?.get(0)?.main?.temp_max,
                            response.body()?.list?.get(0)?.main?.temp_min/*,
                            response.body()?.list?.get(0)?.main?.feels_like)*/))

                        binding.tv2ndForecastTemp.text = String.format(fragmentContext!!.getString(R.string.maxminC,
                            response.body()?.list?.get(1)?.main?.temp_max,
                            response.body()?.list?.get(1)?.main?.temp_min/*,
                            response.body()?.list?.get(1)?.main?.feels_like)*/))

                        binding.tv3rdForecastTemp.text = String.format(fragmentContext!!.getString(R.string.maxminC,
                            response.body()?.list?.get(2)?.main?.temp_max,
                            response.body()?.list?.get(2)?.main?.temp_min/*,
                            response.body()?.list?.get(2)?.main?.feels_like*/))

                        binding.tv4thForecastTemp.text = String.format(fragmentContext!!.getString(R.string.maxminC,
                            response.body()?.list?.get(3)?.main?.temp_max,
                            response.body()?.list?.get(3)?.main?.temp_min/*,
                            response.body()?.list?.get(3)?.main?.feels_like*/))

                        binding.tv5thForecastTemp.text = String.format(fragmentContext!!.getString(R.string.maxminC,
                            response.body()?.list?.get(4)?.main?.temp_max,
                            response.body()?.list?.get(4)?.main?.temp_min/*,
                            response.body()?.list?.get(4)?.main?.feels_like*/))

                        binding.tv6thForecastTemp.text = String.format(fragmentContext!!.getString(R.string.maxminC,
                            response.body()?.list?.get(5)?.main?.temp_max,
                            response.body()?.list?.get(5)?.main?.temp_min/*,
                            response.body()?.list?.get(5)?.main?.feels_like*/))

                        binding.tv7thForecastTemp.text = String.format(fragmentContext!!.getString(R.string.maxminC,
                            response.body()?.list?.get(6)?.main?.temp_max,
                            response.body()?.list?.get(6)?.main?.temp_min/*,
                            response.body()?.list?.get(6)?.main?.feels_like*/))

                    }else{
                        binding.tv1stForecastTemp.text = String.format(fragmentContext!!.getString(R.string.maxminF,
                            response.body()?.list?.get(0)?.main?.temp_max,
                            response.body()?.list?.get(0)?.main?.temp_min/*,
                            response.body()?.list?.get(0)?.main?.feels_like*/))

                        binding.tv2ndForecastTemp.text = String.format(fragmentContext!!.getString(R.string.maxminF,
                            response.body()?.list?.get(1)?.main?.temp_max,
                            response.body()?.list?.get(1)?.main?.temp_min/*,
                            response.body()?.list?.get(1)?.main?.feels_like*/))

                        binding.tv3rdForecastTemp.text = String.format(fragmentContext!!.getString(R.string.maxminF,
                            response.body()?.list?.get(2)?.main?.temp_max,
                            response.body()?.list?.get(2)?.main?.temp_min/*,
                            response.body()?.list?.get(2)?.main?.feels_like*/))

                        binding.tv4thForecastTemp.text = String.format(fragmentContext!!.getString(R.string.maxminF,
                            response.body()?.list?.get(3)?.main?.temp_max,
                            response.body()?.list?.get(3)?.main?.temp_min/*,
                            response.body()?.list?.get(3)?.main?.feels_like*/))

                        binding.tv5thForecastTemp.text = String.format(fragmentContext!!.getString(R.string.maxminF,
                            response.body()?.list?.get(4)?.main?.temp_max,
                            response.body()?.list?.get(4)?.main?.temp_min/*,
                            response.body()?.list?.get(4)?.main?.feels_like*/))

                        binding.tv6thForecastTemp.text = String.format(fragmentContext!!.getString(R.string.maxminF,
                            response.body()?.list?.get(5)?.main?.temp_max,
                            response.body()?.list?.get(5)?.main?.temp_min/*,
                            response.body()?.list?.get(5)?.main?.feels_like*/))

                        binding.tv7thForecastTemp.text = String.format(fragmentContext!!.getString(R.string.maxminF,
                            response.body()?.list?.get(6)?.main?.temp_max,
                            response.body()?.list?.get(6)?.main?.temp_min/*,
                            response.body()?.list?.get(6)?.main?.feels_like*/))
                    }


                    var date = Date(response.body()?.list?.get(2)?.dt?.toLong()!! * 1000)
                    //Log.w("date", date.toString())
                    var sign = if(response.body()?.city?.timezone!! >= 0) "+" else "-"
                    var absTimezone = Math.abs(response.body()?.city?.timezone?.toLong()!! / 3600)
                    val timeZone = TimeZone.getTimeZone("GMT$sign$absTimezone")

                    val sdf = SimpleDateFormat("MMM d, HH:mm")
                    sdf.timeZone = timeZone

                    binding.tv1stForecast.text = sdf.format(Date(response.body()?.list?.get(0)?.dt?.toLong()!! * 1000))
                    binding.tv2ndForecast.text = sdf.format(Date(response.body()?.list?.get(1)?.dt?.toLong()!! * 1000))
                    binding.tv3rdForecast.text = sdf.format(Date(response.body()?.list?.get(2)?.dt?.toLong()!! * 1000))
                    binding.tv4thForecast.text = sdf.format(Date(response.body()?.list?.get(3)?.dt?.toLong()!! * 1000))
                    binding.tv5thForecast.text = sdf.format(Date(response.body()?.list?.get(4)?.dt?.toLong()!! * 1000))
                    binding.tv6thForecast.text = sdf.format(Date(response.body()?.list?.get(5)?.dt?.toLong()!! * 1000))
                    binding.tv7thForecast.text = sdf.format(Date(response.body()?.list?.get(6)?.dt?.toLong()!! * 1000))


                    //Log.w("Datee", sdf.format(response.body()?.list?.get(0)?.dt?.toLong()!! * 1000))
                    //Log.w("Datee", sdf.format(response.body()?.list?.get(1)?.dt?.toLong()!! * 1000))
                    //Log.w("Datee", sdf.format(response.body()?.list?.get(2)?.dt?.toLong()!! * 1000))
                    //Log.w("Datee", sdf.format(response.body()?.list?.get(3)?.dt?.toLong()!! * 1000))
                    //Log.w("Datee", sdf.format(response.body()?.list?.get(4)?.dt?.toLong()!! * 1000))
                    //Log.w("Datee", sdf.format(response.body()?.list?.get(5)?.dt?.toLong()!! * 1000))
                    //Log.w("Datee", sdf.format(response.body()?.list?.get(6)?.dt?.toLong()!! * 1000))

                } else {
                    Toast.makeText(context, "Error: ", Toast.LENGTH_SHORT).show()
                    Log.e("Forecastt", response.errorBody().toString())
                }
            }

            override fun onFailure(
                call: Call<ForecastData?>,
                throwable: Throwable
            ) {
                throwable.printStackTrace()
                Toast.makeText(context, "Error: " + throwable.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


}