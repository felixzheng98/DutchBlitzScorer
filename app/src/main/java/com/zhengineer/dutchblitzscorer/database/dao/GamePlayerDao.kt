package com.zhengineer.dutchblitzscorer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.zhengineer.dutchblitzscorer.database.entities.Game
import com.zhengineer.dutchblitzscorer.database.entities.GamePlayer
import com.zhengineer.dutchblitzscorer.database.entities.PlayerName
import com.zhengineer.dutchblitzscorer.database.multitable.PlayerInfo

@Dao
interface GamePlayerDao {
    @Transaction
    suspend fun startNewGame(
        gameDao: GameDao,
        playerNameDao: PlayerNameDao,
        game: Game,
        players: List<Pair<GamePlayer, PlayerName>>,
    ): Long {
        val gameId = gameDao.addGame(game)
        players.forEach {
            val playerId = playerNameDao.insertPlayerNameIfDoesNotExist(it.second)
            it.first.apply {
                this.gameId = gameId;
                this.playerId = playerId
            }
            addGamePlayer(it.first)
        }
        return gameId
    }

    @Query("SELECT player_id as id, name, player_icon as icon FROM GamePlayer " +
            "LEFT JOIN PlayerName USING(player_id) WHERE game_id = :gameId " +
            "ORDER BY ordinal ASC")
    suspend fun getPlayerInfoByGame(gameId: Long): List<PlayerInfo>

    /**
     * Below are helper queries, which are not used directly by the game repository
     */
    @Insert
    suspend fun addGamePlayer(gamePlayer: GamePlayer)
}