package com.gvugar.tourmate.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gvugar.tourmate.R

class SwipeToRateCallback(
    val context: Context,
    val onRateCallback: (Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    private val rateIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_star)!!
    private val background = ColorDrawable(Color.parseColor("#F9F9F9"))
    private val iconSize = 150 // in pixels (adjust if needed)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onRateCallback(viewHolder.adapterPosition)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val iconMargin = (itemView.height - iconSize) / 2

        if (dX > 0) {
            background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
            background.draw(c)

            val top = itemView.top + iconMargin
            val left = itemView.left + iconMargin

            rateIcon.setBounds(
                left,
                top,
                left + iconSize,
                top + iconSize
            )
            rateIcon.draw(c)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
