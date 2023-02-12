package com.zhengineer.dutchblitzscorer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zhengineer.dutchblitzscorer.database.entities.Game
import com.zhengineer.dutchblitzscorer.database.multitable.GameInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM game WHERE deleted = 0 ORDER BY game_id DESC")
    fun getGames(): Flow<List<Game>>

    @Query("SELECT game_id as gameId, title, COUNT(DISTINCT player_id) as numPlayers, IFNULL(MAX(round), 0) as rounds " +
            "From Game JOIN GamePlayer USING(game_id) " +
            "LEFT JOIN Scores USING(game_id, player_id) " +
            "WHERE deleted = 0 " +
            "GROUP BY game_id " +
            "ORDER BY game_id DESC")
    fun getGameInfos(): Flow<List<GameInfo>>

    @Query("SELECT title from game WHERE game_id = :gameId")
    suspend fun getGameTitle(gameId: Long): String

    @Query("UPDATE game SET deleted = 1, deletedDateUtc = datetime('now') WHERE game_id IN(:gameIds)")
    suspend fun markDeleted(gameIds: List<Long>)

    /**
     * Deletes games that are marked as deleted
     */
    @Query("DELETE FROM game WHERE deleted = 1")
    suspend fun cleanupMarkDeletedGames()

    @Query("DELETE FROM game")
    suspend fun deleteAllGames()

    /**
     * Unused for now - future feature to undelete games
     * Unmarks games as deleted
     */
    @Query("UPDATE game SET deleted = 0, deletedDateUtc = null WHERE game_id IN(:gameIds)")
    suspend fun unmarkDeleted(gameIds: List<Long>)

    /**
     * Unused for now - future feature to undelete games
     * Deletes games that are marked as deleted and are older than the specified number of days
     */
    @Query("DELETE FROM game WHERE deleted = 1 AND deletedDateUtc < datetime('now', '-' ||:days ||  ' days')")
    suspend fun cleanupMarkDeletedGames(days: Int)

    /**
     * Below are helper queries, which are not used directly by the game repository
     */
    @Insert
    suspend fun addGame(game: Game): Long
}