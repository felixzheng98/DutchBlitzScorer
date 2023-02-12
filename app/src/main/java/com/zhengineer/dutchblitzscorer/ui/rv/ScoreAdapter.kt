package com.zhengineer.dutchblitzscorer.ui.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.zhengineer.dutchblitzscorer.R
import com.zhengineer.dutchblitzscorer.common.data.scoreboard.Cell
import com.zhengineer.dutchblitzscorer.common.data.scoreboard.CellType
import com.zhengineer.dutchblitzscorer.common.data.scoreboard.EmptyCell
import com.zhengineer.dutchblitzscorer.common.data.scoreboard.HeaderCell
import com.zhengineer.dutchblitzscorer.common.data.scoreboard.RoundCell
import com.zhengineer.dutchblitzscorer.common.data.scoreboard.ScoreCell
import com.zhengineer.dutchblitzscorer.common.data.scoreboard.TotalCell
import com.zhengineer.dutchblitzscorer.databinding.ListItemEmptyCellBinding
import com.zhengineer.dutchblitzscorer.databinding.ListItemHeaderCellBinding
import com.zhengineer.dutchblitzscorer.databinding.ListItemScoreBinding
import com.zhengineer.dutchblitzscorer.databinding.ListItemTotalCellBinding
import com.zhengineer.dutchblitzscorer.ui.fragments.GameScoringFragment
import com.zhengineer.dutchblitzscorer.ui.fragments.GameScoringFragmentDirections
import com.zhengineer.dutchblitzscorer.viewmodels.GameScoringViewModel

class ScoreAdapter(private var viewModel: GameScoringViewModel) :
    RecyclerView.Adapter<CellBaseViewHolder<Cell>>() {
    private var dataset: List<Cell> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellBaseViewHolder<Cell> {
        val layoutInflater = LayoutInflater.from(parent.context)
        @Suppress("UNCHECKED_CAST")
        return when (CellType.values()[viewType]) {
            CellType.ROUND_CELL ->
                RoundCellViewHolder(
                    ListItemScoreBinding.inflate(
                        layoutInflater,
                        parent,
                        false,
                    ),
                    viewModel
                ) as CellBaseViewHolder<Cell>
            CellType.SCORE_CELL ->
                ScoreCellViewHolder(
                    ListItemScoreBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                ) as CellBaseViewHolder<Cell>
            CellType.EMPTY_CELL -> EmptyCellViewHolder(
                ListItemEmptyCellBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            ) as CellBaseViewHolder<Cell>
            CellType.HEADER_CELL -> HeaderCellViewHolder(
                ListItemHeaderCellBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            ) as CellBaseViewHolder<Cell>
            CellType.TOTAL_CELL -> TotalCellViewHolder(
                ListItemTotalCellBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            ) as CellBaseViewHolder<Cell>
        }

    }

    override fun onBindViewHolder(holder: CellBaseViewHolder<Cell>, position: Int) {
        holder.bind(dataset[position])
    }

    override fun getItemCount(): Int = dataset.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateDataset(dataset: List<Cell>) {
        this.dataset = dataset
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return dataset[position].type.ordinal
    }
}

class RoundCellViewHolder(
    private val binding: ListItemScoreBinding,
    private val viewModel: GameScoringViewModel
) : CellBaseViewHolder<RoundCell>(binding.root) {
    private var round: Int = -1

    init {
        binding.root.setOnLongClickListener {
            GameScoringFragment.dialogEditDelete(it.context, round,
                editAction = {
                    if (round != -1) {
                        viewModel.initRecordRound(true, round)
                        it.findNavController().navigate(
                            GameScoringFragmentDirections.actionGameScoringFragmentToRecordRoundFragment(
                                round
                            )
                        )
                    }
                }, deleteAction = {
                    if (round != -1) {
                        viewModel.deleteRound(round)
                    }
                })
            true
        }
    }

    override fun bind(item: RoundCell) {
        round = item.round
        binding.textViewScore.text = item.round.toString()
    }
}

class ScoreCellViewHolder(private val binding: ListItemScoreBinding) :
    CellBaseViewHolder<ScoreCell>(binding.root) {
    override fun bind(item: ScoreCell) {
        binding.textViewScore.text = item.score.toString()
    }
}

class EmptyCellViewHolder(binding: ListItemEmptyCellBinding) :
    CellBaseViewHolder<EmptyCell>(binding.root) {
    override fun bind(item: EmptyCell) {}
}

class HeaderCellViewHolder(private val binding: ListItemHeaderCellBinding) :
    CellBaseViewHolder<HeaderCell>(binding.root) {

    override fun bind(item: HeaderCell) {
        when (item.position) {
            HeaderCell.Position.TOP_LEFT -> binding.root.setBackgroundResource(R.drawable.shape_top_left_cell)
            HeaderCell.Position.TOP_RIGHT -> binding.root.setBackgroundResource(R.drawable.shape_top_right_cell)
            HeaderCell.Position.TOP_MIDDLE -> binding.root.setBackgroundResource(R.drawable.shape_top_middle_cell)
        }
        binding.textViewHeader.text = item.header
        binding.winningEmoji.visibility = if (item.winning) View.VISIBLE else View.GONE
    }
}

class TotalCellViewHolder(private val binding: ListItemTotalCellBinding) :
    CellBaseViewHolder<TotalCell>(binding.root) {

    override fun bind(item: TotalCell) {
        when (item.position) {
            TotalCell.Position.BOTTOM_RIGHT -> binding.root.setBackgroundResource(R.drawable.shape_bottom_right_cell)
            TotalCell.Position.NORMAL -> binding.root.setBackgroundResource(R.drawable.shape_normal_cell)
        }
        binding.textViewScore.text = item.score.toString()
    }
}