package com.zhengineer.dutchblitzscorer.ui.rv

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class GameItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y) ?: return null

        return object : ItemDetails<Long>() {
            override fun getPosition(): Int =
                recyclerView.getChildViewHolder(view).layoutPosition

            override fun getSelectionKey(): Long = recyclerView.getChildViewHolder(view).itemId
        }
    }
}