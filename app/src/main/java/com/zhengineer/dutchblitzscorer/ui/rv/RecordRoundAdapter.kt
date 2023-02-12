package com.zhengineer.dutchblitzscorer.ui.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.RecyclerView
import com.zhengineer.dutchblitzscorer.common.AdaptiveTextWatcher
import com.zhengineer.dutchblitzscorer.databinding.ListItemRecordRoundBinding
import com.zhengineer.dutchblitzscorer.viewmodels.GameScoringViewModel

class RecordRoundAdapter :
    RecyclerView.Adapter<RecordRoundAdapter.ViewHolder>() {
    private var scores: List<GameScoringViewModel.ScoreKeeper> = emptyList()

    inner class ViewHolder(private val itemBinding: ListItemRecordRoundBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private val adaptiveTextWatcher = AdaptiveTextWatcher(this) { position, text ->
            if (text != null) {
                try {
                    scores[position].score = text.toString().toInt()
                } catch (nfe: NumberFormatException) {
                    scores[position].score = Integer.MIN_VALUE
                }
            }
        }

        init {
            itemBinding.playerScoreEditText.apply {
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        clearFocus()
                    }
                    false
                }
                addTextChangedListener(adaptiveTextWatcher)
            }
        }

        fun bind(score: GameScoringViewModel.ScoreKeeper) {
            itemBinding.playerName.text = score.playerName
            itemBinding.playerScoreEditText.setText(score.score.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemRecordRoundBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scores[position])
    }

    override fun getItemCount(): Int = scores.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(scores: List<GameScoringViewModel.ScoreKeeper>) {
        this.scores = scores
        notifyDataSetChanged()
    }
}