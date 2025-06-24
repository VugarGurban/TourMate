package com.gvugar.tourmate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gvugar.tourmate.Category
import com.gvugar.tourmate.R

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClick: (Category) -> Unit) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>()
{

        private var selectedPosition: Int = 0

        inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val categoryText: TextView = itemView.findViewById(R.id.categoryText)

            fun bind(categoryItem: Category ) {
                categoryText.text = categoryItem.displayName
                categoryText.isSelected = selectedPosition == adapterPosition

                if (selectedPosition == adapterPosition) {
                    categoryText.background = categoryText.context.getDrawable(R.drawable.category_border_selected)
                    categoryText.setTextColor(categoryText.context.getColor(R.color.main_white))
                }
                else {
                    categoryText.background = categoryText.context.getDrawable(R.drawable.category_border_not_selected)
                    categoryText.setTextColor(categoryText.context.getColor(R.color.light_black))
                }


//                categoryText.setBackgroundColor(categoryText.context.getColor( if (selectedPosition == adapterPosition) { R.color.light_black} else { R.color.main_white} ))
//                categoryText.setTextColor(categoryText.context.getColor(if (selectedPosition == adapterPosition){R.color.main_white} else{R.color.light_black}))


                itemView.setOnClickListener {
                    val previewPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previewPosition)
                    notifyItemChanged(selectedPosition)
                    onCategoryClick(categoryItem)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        val categoryItem = Category.valueOf(category.name)
        holder.bind(categoryItem)
    }
}