package com.example.tourmate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.tourmate.Database.AppDatabase
import com.example.tourmate.databinding.FragmentSelectBinding
import com.example.tourmate.entities.CitiesModel
import com.example.tourmate.managers.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectFragment : Fragment() {

    private var binding: FragmentSelectBinding? = null
    private lateinit var dataBaseManager:AppDatabase
    private lateinit var cities: List<CitiesModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding!!.searchBar.threshold = 0

        binding!!.searchBar.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) {
                binding!!.searchBar.showDropDown()
            }
        }
        dataBaseManager = AppDatabase.getInstance(requireContext())

        binding!!.searchButton.setOnClickListener {
            if (binding!!.searchBar.text.toString().isNotEmpty()) {
                val selectedCity = cities.find { it.name == binding!!.searchBar.text.toString() }
                if (selectedCity != null) {
                    //val action = SelectFragmentDirections.actionSelectFragmentToHomeFragment(selectedCity.id)
                    // Navigation.findNavController(it).navigate(action)
                    SharedPreferencesManager.setValue("cityId", selectedCity.id)
                    findNavController().navigate(SelectFragmentDirections.actionSelectFragmentToHomeFragment())
                    SharedPreferencesManager.setValue("isFirstTime", false)
                }else{
                    Toast.makeText(requireContext(), "City not found!", Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(requireContext(), "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {

            cities = dataBaseManager.citiesDao().getAllCities()
            val citiesNames = cities.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, citiesNames)
            withContext(Dispatchers.Main) {
                binding!!.searchBar.setAdapter(adapter)
            }
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}