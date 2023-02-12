package com.zhengineer.dutchblitzscorer.database.repository

import com.zhengineer.dutchblitzscorer.database.dao.GameDao
import com.zhengineer.dutchblitzscorer.database.dao.GamePlayerDao
import com.zhengineer.dutchblitzscorer.database.dao.PlayerNameDao
import com.zhengineer.dutchblitzscorer.database.dao.ScoresDao
import com.zhengineer.dutchblitzscorer.database.entities.Game
import com.zhengineer.dutchblitzscorer.database.entities.GamePlayer
import com.zhengineer.dutchblitzscorer.database.entities.PlayerName
import com.zhengineer.dutchblitzscorer.viewmodels.GameScoringViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val gameDao: GameDao,
    private val gamePlayerDao: GamePlayerDao,
    private val playerNameDao: PlayerNameDao,
    private val scoresDao: ScoresDao
) {
    fun getGameInfos() = gameDao.getGameInfos()

    suspend fun startNewGame(game: Game, players: List<Pair<GamePlayer, PlayerName>>): Long =
        gamePlayerDao.startNewGame(gameDao, playerNameDao, game, players)

    suspend fun getPlayerInfoByGame(gameId: Long) = gamePlayerDao.getPlayerInfoByGame(gameId)

    suspend fun getGameTitle(gameId: Long) = gameDao.getGameTitle(gameId)

    fun getScoresInGame(gameId: Long) = scoresDao.getScoresInGame(gameId)

    suspend fun saveScores(
        gameId: Long, scores: List<GameScoringViewModel.ScoreKeeper>, round: Int? = null
    ) = scoresDao.saveScores(gameId, scores, round)

    suspend fun getScoresInRound(gameId: Long, round: Int) =
        scoresDao.getScoresInRound(gameId, round)

    suspend fun deleteRoundAndUpdateRoundNumbers(gameId: Long, round: Int) =
        scoresDao.deleteRoundAndUpdateRoundNumbers(gameId, round)

    suspend fun markDeleted(gameIds: List<Long>) = gameDao.markDeleted(gameIds)

    suspend fun deleteAllGames() = gameDao.deleteAllGames()

    suspend fun cleanupPlayers() = playerNameDao.cleanupPlayers()
    suspend fun cleanupDeletedGames() = gameDao.cleanupMarkDeletedGames()
}