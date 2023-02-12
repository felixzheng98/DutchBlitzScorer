package com.zhengineer.dutchblitzscorer.database.multitable

import com.zhengineer.dutchblitzscorer.common.data.PlayerIcon

data class PlayerInfo(
    val id: Long,
    val name: String,
    val icon: PlayerIcon? = null,
)