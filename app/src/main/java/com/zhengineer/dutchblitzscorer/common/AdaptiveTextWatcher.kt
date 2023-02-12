package com.zhengineer.dutchblitzscorer.common

import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.RecyclerView

/**
 * A TextWatcher made to use in conjunction with a RecyclerView.ViewHolder
 * It tells the text changes about the position of the view holder in the RecyclerView, so it is
 * possible to update the correct item.
 *
 * This allows for one TextWatcher to be added when the ViewHolder is created instead of repeatedly
 * adding a new one on each onBindViewHolder call.
 */
class AdaptiveTextWatcher(
    private val viewHolder: RecyclerView.ViewHolder,
    private val afterTextChanged: (position: Int, text: Editable?) -> Unit
) : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(p0: Editable?) {
        afterTextChanged.invoke(viewHolder.bindingAdapterPosition, p0)
    }
}