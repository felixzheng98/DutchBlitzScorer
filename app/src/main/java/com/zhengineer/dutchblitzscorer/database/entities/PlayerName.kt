package com.zhengineer.dutchblitzscorer.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Used for remember player names for autocomplete textview and to aggregate player data for
 * an upcoming stats feature. TODO: add stats feature
 */
@Entity(indices = [Index(value = ["name"], unique = true)])
data class PlayerName(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "player_id")
    var player_id: Long = 0,
    @ColumnInfo(name = "name")
    var name: String
)
