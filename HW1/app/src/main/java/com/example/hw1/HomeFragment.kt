package com.example.hw1

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.telecom.Call.Details
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.hw1.adapter.CityAdapter
import com.example.hw1.cityfileData.CitiesItem
import com.example.hw1.data.AppDatabase
import com.example.hw1.data.City
import com.example.hw1.databinding.FragmentHomeBinding
import com.example.hw1.databinding.SearchablespinnerBinding
import com.example.hw1.details.DetailsMoreFragment
import com.example.hw1.details.WeatherDetailsFragment
import com.example.hw1.model.WeatherData
import com.example.hw1.network.NetworkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class HomeFragment : Fragment(), CityAdapter.OnCitySelectedListener{

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: CityAdapter
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var TEMPSTR : String = null.toString()
    private lateinit var placeName : String
    lateinit var arrayList : ArrayList<String> //was : var arrayList : ArrayList<String>? = null
    lateinit var dialog: Dialog //was : var dialog: Dialog? = null
    lateinit var loc : List<CitiesItem> // was : var loc : List<CitiesItem>? = null
    private lateinit var SSBinding : SearchablespinnerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loc = readJsonFile(requireContext(), R.raw.cities)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Log.w("MainActivity", "onCreateView")
        //Log.w("loc", loc.get(0).name)
        //Log.w("locSIze", loc.size.toString())

        placeName = ""
        binding = FragmentHomeBinding.inflate(layoutInflater)
        val unit = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString("unit_preference_title", "Metric")
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        SSBinding = SearchablespinnerBinding.inflate(layoutInflater)

        arrayList = ArrayList()
        /*for(i in 0 until loc!!.size){
            if(loc!![i].name.contains("Budapest")){
                Log.w("Budapest", loc!![i].name)
            }
            arrayList!!.add(loc!![i].name+", "+loc!![i].state_code+", "+loc!![i].country_code)
        }*/
        for(l in loc){
            /*if(l.name.contains("Budapest")){
                Log.w("Budapest", l.name)
            }*/
            arrayList.add(l.name+", "+l.state_code+", "+l.country_code)
        }
        location()

        return binding.root
    }

    fun readJsonFile(context: Context, resourceId: Int): List<CitiesItem> {
        val locations = mutableListOf<CitiesItem>()
        //Log.w("MainActivity", "Reading JSON file from resource id: $resourceId")

        try {
            val inputStream = context.resources.openRawResource(resourceId)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            val json = String(buffer, Charsets.UTF_8)
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val location = CitiesItem(
                    jsonObject.getString("name"),
                    jsonObject.getString("state_code"),
                    jsonObject.getString("state_name"),
                    jsonObject.getString("country_code"),
                    jsonObject.getString("country_name")
                )
                locations.add(location)
                if(jsonObject.getString("name").contains("Budapest")){
                    //w("Budapest", location.name)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("MainActivity", "Error reading JSON file: " + e.localizedMessage)
        }

        return locations
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val unit = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString("unit_preference_title", "Metric")
        val appid = "7d5b2c489ea465597e9d116aed1ca800"

        binding.tvUnit.text = "Budapest, HU"
        binding.fabAddCity.setOnClickListener {
            fabClick()
        }

        binding.fabSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        location()
    }

    private fun fabClick(){
        dialog = Dialog(requireContext())

        if(SSBinding.root.parent != null){
            (SSBinding.root.parent as ViewGroup).removeView(SSBinding.root)
        }
        dialog.setContentView(SSBinding.root)
        dialog.show()

        val adapterr = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            arrayList
        )

        SSBinding.listView.adapter = adapterr


        SSBinding.listView.setOnItemClickListener { parent, view, position, id ->
            binding.tvUnit.text = arrayList[position]
            dialog.dismiss()
            initRecyclerView()
        }

        SSBinding.editText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapterr.filter.filter(s)
            }

            override fun afterTextChanged(s: android.text.Editable) {}
        })

        SSBinding.listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                thread {
                    var newCity = City(null, adapterr.getItem(position).toString(), null.toString(), null.toString(), null.toString())
                    var cityName = adapterr.getItem(position)!!.split(", ")[0]
                    var stateCode = adapterr.getItem(position)!!.split(", ")[1]
                    var countryCode = adapterr.getItem(position)!!.split(", ")[2]

                    for (l in loc){
                        if((l.name == cityName)){
                            //Log.w("MainActivityAA", cityName)
                            //Log.w("MainActivityAA", cityName)
                            //Log.w("MainActivityAA", l.country_code)
                            //Log.w("MainActivityAA", countryCode)
                            if((l.country_code == countryCode)){
                                if(l.state_code == stateCode){
                                    //Log.w("MainActivityAA", l.country_name)
                                    newCity.CountryName = l.country_name
                                    break
                                }
                            }
                        }
                    }
                    AppDatabase.getInstance(requireContext()).cityDao()
                        .insertCityItem(newCity)
                    activity?.runOnUiThread {
                        adapter.addItem(newCity)
                    }
                }
                //Log.w("MainActivity", adapterr.getItem(position).toString())
                dialog.dismiss()
                SSBinding.editText.text.clear()
                //initRecyclerView() // UGLY ASF??
            }
    }

    override fun onCitySelected(city: String?) {
        val showDetailsIntent = activity?.intent
        //Log.w("onCitySelected", city.toString())
        showDetailsIntent?.setClass(requireContext(), WeatherDetailsFragment::class.java)
        showDetailsIntent?.putExtra(WeatherDetailsFragment.EXTRA_CITY_NAME, city)
        findNavController().navigate(R.id.action_homeFragment_to_weatherDetailsFragment)
    }
    //Weird inside function call
    private fun location() {
        //Log.e("location", "location")
        //Log.w("location", "location")
        val task = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            //Log.w("location", "locationretuurn")
            return
        }

        task.addOnSuccessListener { location ->
            //Log.w("location", "addOn")

            if (location != null) {
                //Log.w("First", "First")
                //Log.w("location", location.latitude.toString() + " " + location.longitude.toString())

                getCityByCoord(location.longitude, location.latitude) { placeName ->
                    binding.tvUnit.text = placeName
                    setPlaceStr(placeName)
                    //initRecyclerView() //Ezt szedtem ki legutobb 2023-11-18
                    val showDetailsIntent = activity?.intent
                    showDetailsIntent?.putExtra(WeatherDetailsFragment.CURRENT_LOCATION, binding.tvUnit.text.toString())
                    initRecyclerView()
                }
            }else{ //Emulatoron ez megy le mert nincs location
                initRecyclerView()
            }
        }
    }

    private fun getCityByCoord(lon: Double, lat: Double, callback: (String) -> Unit) {
        NetworkManager.getWeatherByCoord(lat, lon, requireContext())?.enqueue(object : Callback<WeatherData?> {
            override fun onResponse(call: Call<WeatherData?>, response: Response<WeatherData?>) {
                if (response.isSuccessful) {
                    val cityName = response.body()?.name + ", " + response.body()?.sys!!.country
                    //Log.d("CityNameEE", cityName.toString())
                    callback(cityName.toString())
                } else {
                    //Log.d("CityName", "Hiba")
                    Toast.makeText(requireContext(), "Hiba: " + response.code(), Toast.LENGTH_SHORT).show()
                    callback("")
                }
            }

            override fun onFailure(call: Call<WeatherData?>, throwable: Throwable) {
                throwable.printStackTrace()
                Toast.makeText(context, "Hiba: " + throwable.message, Toast.LENGTH_SHORT).show()
                callback("")
            }
        })
    }


    private fun initRecyclerView() {
        //Log.e("initRecyclerView", "initRecyclerView")
        //Log.w("initRecyclerViewWWWWWWWW", "initRecyclerView")
        thread {
            val cities = AppDatabase.getInstance(requireContext()).cityDao().getAllCityItems()

            val temperatureMap = mutableMapOf<Int, String>()

            val totalCities = cities.size
            val latch = CountDownLatch(totalCities)
            val isMetric = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("unit_preference_title", "Metric") == "Metric"

            //Threadben fut ezért ossze keveredik a sorrend
            for (i in 0 until cities.size) {
                val city = cities[i]
                //Log.w("ForcityName", city.CityName)
                NetworkManager.getWeather(city.CityName, requireContext())?.enqueue(object : Callback<WeatherData?> {
                    override fun onResponse(call: Call<WeatherData?>, response: Response<WeatherData?>) {
                        if (response.isSuccessful) {
                            val tempStr = when (isMetric) {
                                true -> String.format(getString(R.string.celsius), response.body()?.main?.temp)
                                false -> String.format(getString(R.string.farenheit), response.body()?.main?.temp)
                            }

                            temperatureMap[i] = tempStr
                            //Log.w("weatherTEMPSSS", tempStr)
                            cities[i].Temperature = tempStr
                            //Log.e("weatherTEMPSSS", cities[i].Temperature)

                            cities[i].Image = response.body()?.weather?.get(0)?.icon.toString()
                        } else {
                            Toast.makeText(requireContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show()
                        }

                        latch.countDown()
                        if (latch.count == 0L) {
                            activity?.runOnUiThread {
                                adapter = CityAdapter(this@HomeFragment, this@HomeFragment, cities)
                                binding.mainRecycler.adapter = adapter
                            }
                        }
                    }

                    override fun onFailure(call: Call<WeatherData?>, throwable: Throwable) {
                        throwable.printStackTrace()
                        Toast.makeText(context, "Error: " + throwable.message, Toast.LENGTH_SHORT).show()
                        latch.countDown()
                    }
                })
            }

            //Log.w("Second", "Second")
            if(cities.isNotEmpty()){
                //Log.w("Cityname", binding.tvUnit.text.toString())
                cities[0].CityName = binding.tvUnit.text.substring(0, binding.tvUnit.text.indexOf(","))
                cities[0].CountryName = binding.tvUnit.text.substring(binding.tvUnit.text.indexOf(",")+2, binding.tvUnit.text.length)
                AppDatabase.getInstance(requireContext()).cityDao().updateCityItem(cities[0])
            }

            activity?.runOnUiThread {
                adapter = CityAdapter(this, this, cities)
                binding.mainRecycler.adapter = adapter
            }
        }
    }

    private fun setPlaceStr(placeStr: String){
        Log.i("InitializePlaceSTR", "Current place: $placeStr")
        placeName = placeStr
    }
    //Nem is kell a függvény, de benthagyom azért
    private fun weather(city: City, callback: (String) -> Unit) : String {
        var tempStr : String = null.toString()
        NetworkManager.getWeather(city.CityName, requireContext())?.enqueue(object :
            Callback<WeatherData?> {
            override fun onResponse(
                call: Call<WeatherData?>,
                response: Response<WeatherData?>
            ) {
                if (response.isSuccessful) {

                    var isMetric = PreferenceManager.getDefaultSharedPreferences(requireContext())
                        .getString("unit_preference_title", "Metric") == "Metric"

                    when(isMetric){
                        true -> {
                            //Log.w("weatherTEMP", response.body()?.main?.temp.toString())
                            tempStr = String.format(getString(R.string.celsius), response.body()?.main?.temp)
                            //Log.w("outtempITE", tempStr)

                        }
                        false -> {
                            Log.w("weatherTEMP", response.body()?.main?.temp.toString())
                            tempStr = String.format(getString(R.string.farenheit), response.body()?.main?.temp)
                            Log.w("outtempITE", tempStr)
                        }
                    }
                    /*Log.w("weather", city.CityName)*/
                    callback(tempStr)
                    writeTempStr(tempStr)

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error: " + response.code(),
                        Toast.LENGTH_SHORT
                    ).show()
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
        //TEMPSTR = idx.toString()
        //Log.w("outtempI", tempStr)
        tempStr = TEMPSTR
        return tempStr
    }



    private fun writeTempStr(tempStr: String) {
        //Log.w("outtempIWR", tempStr)
        TEMPSTR = tempStr
    }

}