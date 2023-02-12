package com.zhengineer.dutchblitzscorer.common.data.scoreboard

class HeaderCell(
    val header: String,
    val position: Position = Position.TOP_MIDDLE,
    var winning: Boolean = false
) : Cell() {
    override val type: CellType = CellType.HEADER_CELL

    enum class Position {
        TOP_LEFT,
        TOP_RIGHT,
        TOP_MIDDLE
    }
}