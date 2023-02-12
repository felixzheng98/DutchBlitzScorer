package com.zhengineer.dutchblitzscorer.common.data.scoreboard

open class ScoreCell(val score: Int): Cell() {
    override val type: CellType = CellType.SCORE_CELL
}