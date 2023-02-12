package com.zhengineer.dutchblitzscorer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.zhengineer.dutchblitzscorer.database.entities.PlayerName

@Dao
interface PlayerNameDao {
    /**
     * Inserts the player name if it does not already exist.
     * It returns the player_id, if name already existed in table it will return the preexisting
     * player_id, otherwise it will return the newly created player_id
     */
    @Transaction
    suspend fun insertPlayerNameIfDoesNotExist(playerName: PlayerName): Long {
        return getPlayerId(playerName.name) ?: addPlayerName(playerName)
    }

    /**
     * Deletes players that are not associated with any game
     */
    @Query("DELETE FROM PlayerName WHERE player_id IN " +
            "(SELECT player_id FROM PlayerName " +
            "   LEFT JOIN GamePlayer USING(player_id) " +
            "   WHERE GamePlayer.player_id IS NULL)")
    suspend fun cleanupPlayers()


    /**
     * Below are helper queries, which are not used directly by the game repository
     */
    @Insert
    suspend fun addPlayerName(playerName: PlayerName): Long

    @Query("SELECT player_id FROM PlayerName WHERE name = :playerName")
    suspend fun getPlayerId(playerName: String): Long?
}