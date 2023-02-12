package com.zhengineer.dutchblitzscorer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.zhengineer.dutchblitzscorer.common.data.scoreboard.Cell
import com.zhengineer.dutchblitzscorer.common.data.scoreboard.EmptyCell
import com.zhengineer.dutchblitzscorer.common.data.scoreboard.HeaderCell
import com.zhengineer.dutchblitzscorer.common.data.scoreboard.RoundCell
import com.zhengineer.dutchblitzscorer.common.data.scoreboard.ScoreCell
import com.zhengineer.dutchblitzscorer.common.data.scoreboard.TotalCell
import com.zhengineer.dutchblitzscorer.common.result.RecordRoundResult
import com.zhengineer.dutchblitzscorer.common.result.error.RecordRoundError
import com.zhengineer.dutchblitzscorer.database.entities.Scores
import com.zhengineer.dutchblitzscorer.database.multitable.PlayerInfo
import com.zhengineer.dutchblitzscorer.database.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
class GameScoringViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val crashlytics: FirebaseCrashlytics,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val gameId: Long = savedStateHandle[GAME_ID_KEY]!!
    val players: LiveData<List<PlayerInfo>> =
        liveData { emit(gameRepository.getPlayerInfoByGame(gameId)) }
    val gameTitle = liveData {
        emit(gameRepository.getGameTitle(gameId))
    }

    private val combinedFlow: Flow<List<Cell>> =
        players.asFlow().combine(gameRepository.getScoresInGame(gameId), ::formatCells)
    val scores: LiveData<List<Cell>> = combinedFlow.asLiveData()
    val recordRound: MutableLiveData<List<ScoreKeeper>> = MutableLiveData()

    fun initRecordRound(edit: Boolean = false, round: Int = -1) {
        recordRound.value = emptyList()
        if (edit) {
            viewModelScope.launch {
                val scores = gameRepository.getScoresInRound(gameId, round)
                recordRound.postValue(
                    players.value!!.mapIndexed { index, player ->
                        ScoreKeeper(
                            player.id,
                            player.name,
                            scores[index]
                        )
                    }
                )
            }
        } else {
            recordRound.value =
                players.value!!.map { player -> ScoreKeeper(player.id, player.name, 0) }
        }

    }

    fun deleteRound(round: Int) {
        viewModelScope.launch {
            gameRepository.deleteRoundAndUpdateRoundNumbers(gameId, round)
        }
    }

    fun recordRound(round: Int? = null): RecordRoundResult {
        val scores: List<ScoreKeeper>? = recordRound.value
        if (scores != null) {
            scores.forEach {
                if (it.score == Integer.MIN_VALUE) {
                    return RecordRoundResult.Error(RecordRoundError.INVALID_INPUT)
                }
            }
            try {
                runBlocking {
                    gameRepository.saveScores(gameId, scores, round)
                }
            } catch (e: Exception) {
                crashlytics.recordException(e)
                return RecordRoundResult.Error(RecordRoundError.DATABASE_ERROR, e)
            }
        } else {
            throw IllegalStateException("No scores to record.")
        }
        return RecordRoundResult.Success
    }

    fun recordRoundAllZeroScores(): Boolean {
        val values = recordRound.value ?: return false
        values.forEach {
            if (it.score != 0) {
                return false
            }
        }
        return true
    }

    private fun formatCells(players: List<PlayerInfo>?, scores: List<Scores>?): List<Cell> {
        if (players == null) {
            return emptyList()
        }

        val sum = IntArray(players.size)

        val playerCells: MutableList<HeaderCell> = ArrayList()
        players.forEachIndexed { index, player ->
            if (index == players.size - 1) {
                playerCells.add(HeaderCell(player.name, HeaderCell.Position.TOP_RIGHT))
            } else {
                playerCells.add(HeaderCell(player.name))
            }
        }

        val scoreAndRoundCells: MutableList<Cell> = ArrayList()
        if (!scores.isNullOrEmpty()) {
            scores.forEachIndexed { index, score ->
                if (index % players.size == 0) {
                    scoreAndRoundCells.add(RoundCell(score.round))
                }
                sum[index % players.size] += score.score
                scoreAndRoundCells.add(ScoreCell(score.score))
            }
        }

        val totalCells: MutableList<TotalCell> = ArrayList()
        val highestScore = sum.max()
        sum.forEachIndexed { index, s ->
            if (index == sum.size - 1) {
                totalCells.add(TotalCell(s, TotalCell.Position.BOTTOM_RIGHT))
            } else {
                totalCells.add(TotalCell(s))
            }

            if (highestScore != 0 && s == highestScore) {
                playerCells[index].winning = true
            }
        }

        val list: MutableList<Cell> = ArrayList()
        with(list){
            add(HeaderCell("Round", HeaderCell.Position.TOP_LEFT))
            addAll(playerCells)
            addAll(scoreAndRoundCells)
            add(EmptyCell())
            addAll(totalCells)
        }
        return list
    }

    data class ScoreKeeper(
        val playerId: Long,
        val playerName: String,
        var score: Int,
    )

    companion object {
        // Nav args keys
        const val GAME_ID_KEY = "gameId"
    }
}