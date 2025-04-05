package com.example.tourmate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tourmate.Category
import com.example.tourmate.R
import com.example.tourmate.entities.PlacesModel

class PlacesAdapter(private val placesList: MutableList<PlacesModel>): RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {

    var itemClickListener:((Int)->Unit)?=null

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val placeImage = itemView.findViewById<ImageView>(R.id.placeImage)
        private val placeName = itemView.findViewById<TextView>(R.id.placeName)
        private val categoryName = itemView.findViewById<TextView>(R.id.categoryName)
        private val location = itemView.findViewById<TextView>(R.id.location)
        private val ratingText = itemView.findViewById<TextView>(R.id.rating_text)



        //secondary
        private val timeText = itemView.findViewById<TextView>(R.id.time_text)
        private val distanceText = itemView.findViewById<TextView>(R.id.distance_text)


        fun bind(place: PlacesModel){
            placeName.text = place.name
            categoryName.text = Category.fromId(place.category_id).displayName
            location.text = place.address
            ratingText.text = place.rate.toString()

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return placesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placesList[position]
        holder.bind(place)

        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(place.id)
        }

    }

    fun updateData(newPlacesList: List<PlacesModel>) {
        placesList.clear()
        placesList.addAll(newPlacesList)
        notifyDataSetChanged()
    }
}