package com.example.tourmate

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tourmate.Database.AppDatabase
import com.example.tourmate.adapters.CategoryAdapter
import com.example.tourmate.adapters.PlacesAdapter
import com.example.tourmate.databinding.FragmentHomeBinding
import com.example.tourmate.entities.PlacesModel
import com.example.tourmate.managers.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.tourmate.utils.SwipeToRateCallback
import java.math.RoundingMode


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: AppDatabase
    private lateinit var placesAdapter: PlacesAdapter
    private var cityId = SharedPreferencesManager.getValue("cityId",1)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as NavigationListener).changeBottomMenuVisibility(View.VISIBLE)
        //val bundle: HomeFragmentArgs by navArgs()
        database = AppDatabase.getInstance(requireContext())

        binding.searchBar.threshold = 0
        binding.searchBar.setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) {
                binding.searchBar.showDropDown()
            }
        }

        lifecycleScope.launch (Dispatchers.IO) {
            val cityName = database.citiesDao().getCity(cityId).name
            withContext(Dispatchers.Main){
                binding.textView.text = cityName
            }
        }

        val categoryList: List<Category> = listOf(Category.ALL, Category.HISTORICAL, Category.MUSEUMS, Category.PUBLIC)
        val recyclerView1 = view.findViewById<RecyclerView>(R.id.recyclerView1)
        recyclerView1.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView1.adapter = CategoryAdapter(categoryList, { category -> onCategoryClick(category) })

        val recyclerView2 = view.findViewById<RecyclerView>(R.id.recyclerView2)
        recyclerView2.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch (Dispatchers.IO){
            val placesList = database.placesDao().getPlaceByCityId(cityId)
            placesAdapter = PlacesAdapter(placesList)
            recyclerView2.adapter = placesAdapter


            placesAdapter.itemClickListener = {id->
                val placeId = placesList.find { it.id == id }!!.id
                val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(placeId)
                view.findNavController().navigate(action)
            }
        }

        lifecycleScope.launch (Dispatchers.IO){
            val cities = database.citiesDao().getAllCities()
            val citiesNames = cities.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, citiesNames)
            withContext(Dispatchers.Main) {
                binding.searchBar.setAdapter(adapter)
            }
        }

        binding.searchBar.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filterPlaces(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.chatbotBtn.setOnClickListener {
            lifecycleScope.launch (Dispatchers.IO){
                val city = database.citiesDao().getCity(cityId)
                val action = HomeFragmentDirections.actionHomeFragmentToChatbotFragment(city.name)
                withContext(Dispatchers.Main){
                    view.findNavController().navigate(action)
                }
            }
        }

        val swipeToRateCallback = SwipeToRateCallback(requireContext()) { position ->
            val place = placesAdapter.getPlaceAt(position)
            RatingDialog{newRating->
                lifecycleScope.launch(Dispatchers.IO) {
                    val placeEntity = database.placesDao().getPlaceById(place.id)
                    val updatedRating = ((placeEntity.rate + newRating)/2).toBigDecimal().setScale(1, RoundingMode.HALF_UP).toFloat()
                    placeEntity.rate = updatedRating
                    database.placesDao().updatePlace(placeEntity)

                    val updatedPlaces = database.placesDao().getPlaceByCityId(cityId)
                    withContext(Dispatchers.Main){
                        placesAdapter.updateData(updatedPlaces)
                    }
                }
            }.show(parentFragmentManager, "RatingDialog")

            placesAdapter.notifyItemChanged(position)

        }

        val itemTouchHelper = ItemTouchHelper(swipeToRateCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView2)
    }

    private fun filterPlaces(text: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val selectedCity = database.citiesDao().getAllCities().find { it.name == text }

            if (text.isNotEmpty() && selectedCity != null) {
                val filteredPlaces:List<PlacesModel> = database.placesDao().getPlaceByCityId(selectedCity.id)
                withContext(Dispatchers.Main) {
                    binding.textView.text = selectedCity.name
                    placesAdapter.updateData(filteredPlaces)
                    cityId = selectedCity.id
                    SharedPreferencesManager.setValue("cityId", selectedCity.id)
                }
            }
        }
    }

    private fun onCategoryClick(category: Category) {
        lifecycleScope.launch (Dispatchers.IO){

            val placesList = if (category.id == Category.ALL.id) {
                database.placesDao().getPlaceByCityId(cityId)
            }else{
                database.placesDao().getPlaceByCategory(category.id, cityId)
            }
            withContext(Dispatchers.Main){
                placesAdapter.updateData(placesList)
            }
        }

    }
}
