package com.zhengineer.dutchblitzscorer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.zhengineer.dutchblitzscorer.common.Constants
import com.zhengineer.dutchblitzscorer.common.result.CreateGameResult
import com.zhengineer.dutchblitzscorer.common.result.error.CreateGameError
import com.zhengineer.dutchblitzscorer.database.entities.Game
import com.zhengineer.dutchblitzscorer.database.entities.GamePlayer
import com.zhengineer.dutchblitzscorer.database.entities.PlayerName
import com.zhengineer.dutchblitzscorer.database.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.runBlocking

@HiltViewModel
class CreateGameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val crashlytics: FirebaseCrashlytics
) : ViewModel() {
    var gameTitle: String
    var numPlayers = MutableLiveData(Constants.DEFAULT_PLAYERS)
    val players: Array<Pair<GamePlayer, PlayerName>>

    init {
        val currentTime: Date = Calendar.getInstance().time
        val simpleDateFormat = SimpleDateFormat(Constants.DATE_FORMAT_STRING, Locale.getDefault())
        gameTitle = "Game on ${simpleDateFormat.format(currentTime)}"

        players = Array(Constants.MAX_PLAYERS) {
            Pair(
                GamePlayer(
                    ordinal = it + 1
                ),
                PlayerName(name = "Player ${it + 1}")
            )
        }
    }

    fun createGame(): CreateGameResult {
        // check if player names are empty
        for (i in 0 until numPlayers.value!!) {
            if (players[i].second.name.isBlank()) {
                return CreateGameResult.Error(CreateGameError.PlayerNameEmpty)
            }
        }

        // check if there are duplicate names
        val nameSet = mutableSetOf<String>()
        for (i in 0 until numPlayers.value!!) {
            if (nameSet.contains(players[i].second.name)) {
                return CreateGameResult.Error(CreateGameError.DuplicateName)
            }
            nameSet.add(players[i].second.name)
        }


        return try {
            CreateGameResult.Success(runBlocking {
                return@runBlocking gameRepository.startNewGame(
                    Game(title = gameTitle),
                    players.toList().subList(0, numPlayers.value!!)
                )
            })
        } catch (e: Exception) {
            crashlytics.recordException(e)
            CreateGameResult.Error(CreateGameError.DatabaseError, e)
        }
    }
}