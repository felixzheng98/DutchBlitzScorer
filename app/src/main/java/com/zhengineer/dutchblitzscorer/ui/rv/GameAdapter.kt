package com.zhengineer.dutchblitzscorer.ui.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.zhengineer.dutchblitzscorer.R
import com.zhengineer.dutchblitzscorer.database.multitable.GameInfo
import com.zhengineer.dutchblitzscorer.databinding.ListItemGameBinding
import com.zhengineer.dutchblitzscorer.ui.fragments.MainFragmentDirections

class GameAdapter(private var dataset: List<GameInfo>) :
    RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    class ViewHolder(private val itemBinding: ListItemGameBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private var game: GameInfo? = null

        init {
            itemBinding.root.setOnClickListener {
                game?.let {
                    Navigation.findNavController(itemView)
                        .navigate(MainFragmentDirections.actionMainFragmentToNavGraphScoring(it.gameId))
                }
            }
        }

        fun bind(game: GameInfo, selected: Boolean, anySelected: Boolean) {
            this.game = game
            itemBinding.title.text = game.title
            itemBinding.description.text = itemView.context.getString(
                R.string.game_description,
                game.numPlayers,
                game.rounds
            )
            itemBinding.checkboxSelected.visibility = if (anySelected) View.VISIBLE else View.GONE
            itemBinding.checkboxSelected.isChecked = selected
        }

    }

    override fun getItemId(position: Int): Long {
        return dataset[position].gameId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            ListItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            dataset[position],
            tracker?.isSelected(getItemId(position)) ?: false,
            !(tracker?.selection?.isEmpty ?: true)
        )
    }

    override fun getItemCount(): Int = dataset.size

    fun updateDataset(newDataset: List<GameInfo>) {
        dataset = newDataset
        notifyDataSetChanged()
    }
}