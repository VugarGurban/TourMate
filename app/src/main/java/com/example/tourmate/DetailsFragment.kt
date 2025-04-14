package com.example.tourmate

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.tourmate.Database.AppDatabase
import com.example.tourmate.databinding.FragmentDetailsBinding
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.example.tourmate.utils.getResourceIdByName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var database: AppDatabase
    private var isLiked = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = AppDatabase.getInstance(requireContext())
        val bundle: DetailsFragmentArgs by navArgs()
        (activity as NavigationListener).changeBottomMenuVisibility(View.GONE)

        lifecycleScope.launch (Dispatchers.IO){
            val place = database.placesDao().getPlaceById(bundle.placeId)

            val locationUrl = place.link
            binding.location.setOnClickListener {
                goToUrl(locationUrl)
            }

            withContext(Dispatchers.Main){
                binding.ratingText.text = place.rate.toString()
                binding.descriptionText.text = place.description
                binding.placeName.text = place.name
                binding.addressText.text = place.address
                val category = Category.fromId(place.category_id)
                binding.categoryText.text = category.displayName
                binding.distanceText.text = "~ km"
                binding.timeText.text = "~ min"
                binding.placeImage.setImageResource(getResourceIdByName(binding.placeImage.context,place.image_path))
                updateFavoriteButton(place.is_liked)

                binding.favouritesBtn.setOnClickListener {
                    isLiked = if (isLiked == 0) 1 else 0

                    lifecycleScope.launch (Dispatchers.IO){
                        database.placesDao().updateIsLiked(place.id, isLiked)
                    }
                    updateFavoriteButton(isLiked)
                }
            }
        }

        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun goToUrl(link: String){
        val intent = Intent(Intent.ACTION_VIEW, link.toUri())
        startActivity(intent)
    }

    private fun updateFavoriteButton(isLiked: Int){
        if (isLiked == 1){
            binding.favouritesBtn.setImageResource(R.drawable.add_to_favourites_selected)
        } else {
            binding.favouritesBtn.setImageResource(R.drawable.add_to_favourites)

        }
    }
}