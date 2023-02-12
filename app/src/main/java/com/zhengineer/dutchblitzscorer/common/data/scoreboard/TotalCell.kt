package com.zhengineer.dutchblitzscorer.common.data.scoreboard

class TotalCell(score: Int, val position: Position = Position.NORMAL): ScoreCell(score) {
    override val type: CellType = CellType.TOTAL_CELL

    enum class Position {
        BOTTOM_RIGHT,
        NORMAL
    }
}