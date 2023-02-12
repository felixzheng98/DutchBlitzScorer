package com.zhengineer.dutchblitzscorer.ui.rv

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.zhengineer.dutchblitzscorer.common.data.scoreboard.Cell

abstract class CellBaseViewHolder<T: Cell>(root: View) : RecyclerView.ViewHolder(root) {
    abstract fun bind(item: T)
}