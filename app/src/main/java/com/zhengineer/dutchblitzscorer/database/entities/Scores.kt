package com.zhengineer.dutchblitzscorer.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["round", "game_id", "player_id"],
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
data class Scores(
    val round: Int,
    val game_id: Long,
    val player_id: Long,
    val score: Int,
)
