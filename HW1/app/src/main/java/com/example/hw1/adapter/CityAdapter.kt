package com.example.hw1.adapter

import android.content.Context
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.hw1.HomeFragment
import com.example.hw1.R
import com.example.hw1.data.AppDatabase
import com.example.hw1.data.City
import com.example.hw1.databinding.CityRowBinding
import kotlin.concurrent.thread
import kotlin.math.log

class CityAdapter(private val context: HomeFragment, private val listener: OnCitySelectedListener, itemsList: List<City>) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    private val items = mutableListOf<City>()
    private lateinit var settingsPreference: SwitchPreferenceCompat

    init {
        items.addAll(itemsList)
    }


    interface OnCitySelectedListener {
        fun onCitySelected(city: String?)
    }


    operator fun get(index: Int){
        items[index]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var LayoutInflater = LayoutInflater.from(parent.context)
        val cityRowBinding = CityRowBinding.inflate(
            LayoutInflater,parent,false
        )
        cityRowBinding.Image.setImageResource(R.mipmap.ic_wicon_foreground)
        return ViewHolder(cityRowBinding)
    }



    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[holder.adapterPosition])
        //Get the animations settings prefernece
        val animationPreference = PreferenceManager.getDefaultSharedPreferences(context.requireContext()).getString("animation_preference_title", "Off") == "On"
        if(animationPreference){
            holder.binding.cityCard.startAnimation(android.view.animation.AnimationUtils.loadAnimation(context.requireContext(), R.anim.slide_in))
        }
        //holder.binding.cityCard.startAnimation(android.view.animation.AnimationUtils.loadAnimation(context.requireContext(), R.anim.slide_in))
        /*if(items[position].Image == "null"){
            holder.bind(items[holder.adapterPosition])
            holder.binding.cityCard.startAnimation(android.view.animation.AnimationUtils.loadAnimation(context.requireContext(), R.anim.slide_in))
        }else{
            holder.bind(items[holder.adapterPosition])
        }*/
    }

    fun addItem(city: City) {
        items.add(city)
        //Log.w("addItem", items.lastIndex.toString())
        notifyItemInserted(items.lastIndex)
    }

    fun delItem(pos: Int) {
        val city = items[pos]
        items.removeAt(pos)
        notifyItemRemoved(pos)
        thread {
            AppDatabase.getInstance(context.requireContext()).cityDao().deleteCityItem(city)
        }
    }

    inner class ViewHolder(val binding: CityRowBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.delete.setOnClickListener {
                //Log.w("CityDelete", "Delete clicked${adapterPosition}" +" ${items[adapterPosition].CityId}")
                delItem(adapterPosition)
            }
            binding.cityCard.setOnClickListener {
                //Log.w("CityAdapter", "CityCard clicked")
                listener.onCitySelected(items[adapterPosition].CityName)


            }
        }

        fun bind(city: City){
            binding.cityName.text = city.CityName
            binding.countryName.text = city.CountryName
            binding.temperature.text = city.Temperature
            //Log.w("CITY", city.Image)
            if(city.Image != "null"){ //Azért kell hogy ne dobáljon fogyatékos errorokat
                Glide.with(context!!)
                    .load("https://openweathermap.org/img/w/${city.Image}.png")
                    .transition(DrawableTransitionOptions().crossFade())
                    .into(binding.Image)
            }
        }

    }
}