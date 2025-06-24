package com.gvugar.tourmate

import android.app.Dialog
import android.os.Bundle
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class RatingDialog(
    private val onRatingSelected: (Float) -> Unit
): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_rating, null)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        builder.setView(view)
            .setPositiveButton("Submit") { _, _ ->
                onRatingSelected(ratingBar.rating)
            }
            .setNegativeButton("Cancel") { _, _ ->
                dismiss()
            }
        return builder.create()
    }
}