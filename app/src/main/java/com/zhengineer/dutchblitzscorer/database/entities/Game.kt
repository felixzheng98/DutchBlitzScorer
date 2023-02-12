package com.zhengineer.dutchblitzscorer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Game(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "game_id")
    val gameId: Long = 0,
    val title: String,
    val deleted: Boolean = false,
    val deletedDateUtc: String? = null,
)