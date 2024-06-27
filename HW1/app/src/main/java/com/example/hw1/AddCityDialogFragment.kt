package com.example.hw1

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import com.example.hw1.adapter.CityAdapter
import com.example.hw1.data.AppDatabase
import com.example.hw1.data.City
import com.example.hw1.databinding.FragmentAddCityDialogBinding
import kotlin.concurrent.thread

class AddCityDialogFragment(adapter : CityAdapter) : DialogFragment() {


    interface AddCityDialogListener {
        fun onCityAdded(city: String?)
    }

    private lateinit var adapter: CityAdapter

    init {
        this.adapter = adapter
    }

    private fun isValid() = binding.NewCityDialogEditText.text.isNotEmpty()

    private lateinit var binding: FragmentAddCityDialogBinding
    private lateinit var listener: AddCityDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //binding = FragmentAddCityDialogBinding.inflate(LayoutInflater.from(context))
        listener = requireContext() as? AddCityDialogListener
            ?: throw RuntimeException("Activity must implement the AddCityDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentAddCityDialogBinding.inflate(layoutInflater)
        binding.NewCityDialogSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.cities)
        )
        binding.NewCityDialogAutoCompleteTextView.setAdapter(ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.cities)
        ))
        return AlertDialog.Builder(requireContext())
            .setTitle("New City")
            .setView(binding.root)
            .setPositiveButton("Ok") { _, _ ->
                thread {
                    var newCity = City(null, binding.NewCityDialogSpinner.selectedItem.toString(), null.toString(), null.toString(), null.toString())
                    //Nem az interfacet használom, mert rossz
                    AppDatabase.getInstance(requireActivity().applicationContext).cityDao()
                        .insertCityItem(newCity)
                    activity?.runOnUiThread {
                        adapter.addItem(newCity)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    companion object{
        const val TAG = "AddCityDialogFragment"
    }


}