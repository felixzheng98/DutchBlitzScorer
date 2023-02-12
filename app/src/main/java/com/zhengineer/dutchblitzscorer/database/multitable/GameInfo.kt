package com.zhengineer.dutchblitzscorer.database.multitable

data class GameInfo(
    val gameId: Long,
    val title: String,
    val rounds: Int,
    val numPlayers: Int,
)