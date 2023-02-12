package com.zhengineer.dutchblitzscorer.ui.rv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.RecyclerView
import com.zhengineer.commons.util.Utils.hideKeyboard
import com.zhengineer.dutchblitzscorer.R
import com.zhengineer.dutchblitzscorer.common.AdaptiveTextWatcher
import com.zhengineer.dutchblitzscorer.common.Constants.WHITE_SPACE_REGEX
import com.zhengineer.dutchblitzscorer.database.entities.GamePlayer
import com.zhengineer.dutchblitzscorer.database.entities.PlayerName
import com.zhengineer.dutchblitzscorer.databinding.ListItemPlayerInfoBinding

class PlayerInfoAdapter(private val players: Array<Pair<GamePlayer, PlayerName>>) :
    RecyclerView.Adapter<PlayerInfoAdapter.ViewHolder>() {
    private var numPlayers = 0

    inner class ViewHolder(private val binding: ListItemPlayerInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val adaptiveTextWatcher = AdaptiveTextWatcher(this) { position, text ->
            if (text != null) {
                players[position].second.name =
                    text.toString().trim().replace(WHITE_SPACE_REGEX, " ")
            }
        }

        init {
            binding.autoTextViewPlayerName.apply {
                addTextChangedListener(adaptiveTextWatcher)
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        hideKeyboard()
                        clearFocus()
                    }
                    false
                }
            }
        }

        fun bind(playerInfo: Pair<GamePlayer, PlayerName>) {
            binding.playerInfo.apply {
                hint = itemView.context.getString(R.string.player_name, playerInfo.first.ordinal)
            }

            binding.autoTextViewPlayerName.apply {
                setText(playerInfo.second.name, false)
                // ACTION_DONE if it is the last item, otherwise ACTION_NEXT to jump to next
                imeOptions = if (bindingAdapterPosition == numPlayers - 1) {
                    EditorInfo.IME_ACTION_DONE
                } else {
                    EditorInfo.IME_ACTION_NEXT
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemPlayerInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(players[position])
    }

    override fun getItemCount(): Int = numPlayers

    @SuppressLint("NotifyDataSetChanged")
    fun updatePlayerCount(numPlayers: Int) {
        this.numPlayers = numPlayers
        notifyDataSetChanged()
    }
}