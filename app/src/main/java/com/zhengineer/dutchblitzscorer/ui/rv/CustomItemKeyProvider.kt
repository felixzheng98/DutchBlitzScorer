package com.zhengineer.dutchblitzscorer.ui.rv

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

class CustomItemKeyProvider(private val recyclerView: RecyclerView, private val adapter: GameAdapter):ItemKeyProvider<Long>(SCOPE_MAPPED) {
    override fun getKey(position: Int): Long {
        return adapter.getItemId(position)
    }

    override fun getPosition(key: Long): Int {
        val viewHolder: RecyclerView.ViewHolder? = recyclerView.findViewHolderForItemId(key)
        return viewHolder?.layoutPosition ?: RecyclerView.NO_POSITION
    }
}