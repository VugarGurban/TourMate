package com.example.tourmate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tourmate.adapters.CategoryAdapter


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as NavigationListener).changeBottomMenuVisibility(View.VISIBLE)

        val categoryList: List<Category> = listOf(Category.ALL, Category.HISTORICAL, Category.MUSEUMS, Category.PUBLIC)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView1)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = CategoryAdapter(categoryList, { category -> onCategoryClick(category) })

    }

    private fun onCategoryClick(category: Category) {
        when (category) {
            Category.ALL -> {
                Toast.makeText(requireContext(), "All", Toast.LENGTH_SHORT).show()
            }

            Category.HISTORICAL -> {
                Toast.makeText(requireContext(), "Historical", Toast.LENGTH_SHORT).show()
            }

            Category.MUSEUMS -> {
                Toast.makeText(requireContext(), "Museums", Toast.LENGTH_SHORT).show()
            }

            Category.PUBLIC -> {
                Toast.makeText(requireContext(), "Public", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
