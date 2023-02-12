package com.zhengineer.dutchblitzscorer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.zhengineer.dutchblitzscorer.common.data.PlayerIcon

/**
 * The players associated with the game.
 *
 * Player names must be unique for each game.
 */
@Entity(
    primaryKeys = ["game_id", "player_id"],
    indices = [Index("game_id"), Index("player_id")],
    foreignKeys = [
        ForeignKey(
            entity = Game::class,
            parentColumns = ["game_id"],
            childColumns = ["game_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PlayerName::class,
            parentColumns = ["player_id"],
            childColumns = ["player_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class GamePlayer(
    @ColumnInfo(name = "game_id")
    var gameId: Long = -1, // This will be overwritten when inserting with the actual game id
    var ordinal: Int, // Whether they are player 1,2,3, etc in the game
    @ColumnInfo(name = "player_id")
    var playerId: Long = -1,
    @ColumnInfo(name = "player_icon")
    var playerIcon: PlayerIcon? = null
)