package com.zhengineer.dutchblitzscorer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.zhengineer.dutchblitzscorer.database.entities.Scores
import com.zhengineer.dutchblitzscorer.viewmodels.GameScoringViewModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoresDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: Scores)

    @Transaction
    suspend fun saveScores(
        gameId: Long,
        scores: List<GameScoringViewModel.ScoreKeeper>,
        round: Int? = null
    ) {
        val currentRound = round ?: (latestRoundInGame(gameId) + 1)

        scores.forEach {
            val score = Scores(currentRound, gameId, it.playerId, it.score)
            insertScore(score)
        }
    }

    @RewriteQueriesToDropUnusedColumns
    @Query(
        "SELECT * FROM Scores " +
                "LEFT JOIN GamePlayer USING(game_id, player_id) " +
                "WHERE game_id = :gameId " +
                "ORDER BY round, ordinal ASC"
    )
    fun getScoresInGame(gameId: Long): Flow<List<Scores>?>

    @Transaction
    suspend fun deleteRoundAndUpdateRoundNumbers(gameId: Long, round: Int) {
        deleteRound(gameId, round)
        updateRoundNumbers(gameId, round)
    }

    @Query(
        "SELECT score FROM Scores " +
                "JOIN GamePlayer USING(player_id, game_id) " +
                "WHERE game_id = :gameId AND round = :round " +
                "ORDER BY ordinal ASC"
    )
    suspend fun getScoresInRound(gameId: Long, round: Int): List<Int>

    /**
     * Below are helper queries, which are not used directly by the game repository
     */
    @Query("SELECT IFNULL(MAX(round), 0) FROM Scores WHERE game_id = :gameId")
    suspend fun latestRoundInGame(gameId: Long): Int

    @Query("DELETE FROM Scores WHERE game_id = :gameId AND round = :round")
    suspend fun deleteRound(gameId: Long, round: Int)

    @Query("UPDATE Scores SET round = round - 1 WHERE game_id = :gameId AND round > :round")
    suspend fun updateRoundNumbers(gameId: Long, round: Int)
}