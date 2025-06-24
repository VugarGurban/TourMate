package com.gvugar.tourmate.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gvugar.tourmate.Suggestion
import com.gvugar.tourmate.databinding.SuggestionItemBinding

class SuggestionAdapter(
    private val suggestions: List<Suggestion>,
    private val onClick: (String) -> Unit) : RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder>() {
   inner class SuggestionViewHolder(binding: SuggestionItemBinding) : RecyclerView.ViewHolder(binding.root) {
       val content = binding.suggestionContentText
       val description = binding.suggestionDescriptionText
   }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SuggestionAdapter.SuggestionViewHolder {
        val view = SuggestionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionAdapter.SuggestionViewHolder, position: Int) {
        val suggestion = suggestions[position]
        holder.content.text = suggestion.content
        holder.description.text = suggestion.description
        holder.itemView.setOnClickListener {
            onClick(suggestion.content)
        }
    }

    override fun getItemCount(): Int {
        return suggestions.size
    }

}