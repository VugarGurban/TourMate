package com.example.tourmate

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tourmate.Database.AppDatabase
import com.example.tourmate.adapters.PlacesAdapter
import com.example.tourmate.databinding.FragmentFavouritesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.navigation.findNavController

class FavouritesFragment : Fragment() {
    private lateinit var binding: FragmentFavouritesBinding
    private lateinit var database: AppDatabase
    private lateinit var placesAdapter: PlacesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = AppDatabase.getInstance(requireContext())
        (activity as NavigationListener).changeBottomMenuVisibility(View.VISIBLE)
        binding.recyclerView1.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch (Dispatchers.IO){
            val placesList = database.placesDao().getLikedPlaces()
            placesAdapter = PlacesAdapter(placesList)
            binding.recyclerView1.adapter = placesAdapter
            withContext(Dispatchers.Main){
                if (placesList.isEmpty()){
                    binding.notFoundLayout.visibility = View.VISIBLE
                } else{
                    binding.notFoundLayout.visibility = View.GONE
                }
            }
            placesAdapter.itemClickListener = {id->
                val placeId = placesList.find { it.id == id }!!.id
                val action = FavouritesFragmentDirections.actionFavouritesFragmentToDetailsFragment(placeId)
                view.findNavController().navigate(action)
            }
        }

        binding.searchBar.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val query = p0.toString().trim().lowercase()

                lifecycleScope.launch(Dispatchers.IO) {
                    val favoritePlaces = database.placesDao().getLikedPlaces()
                    val filteredPlaces = favoritePlaces.filter { place ->
                        place.name.lowercase().contains(query) ||
                                database.citiesDao().getCity(place.city_id).name.lowercase()
                                    .contains(query) ||
                                Category.fromId(place.category_id).displayName.lowercase()
                                    .contains(query)
                    }
                    withContext(Dispatchers.Main) {
                        placesAdapter.updateData(filteredPlaces)
                        binding.notFoundLayout.visibility = if (filteredPlaces.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
            }

        })

    }
}