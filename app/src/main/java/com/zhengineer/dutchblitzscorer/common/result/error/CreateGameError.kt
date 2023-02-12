package com.zhengineer.dutchblitzscorer.common.result.error

import androidx.annotation.StringRes
import com.zhengineer.dutchblitzscorer.R

enum class CreateGameError(
    @StringRes val errorDialogTitle: Int,
    @StringRes val errorDialogMessage: Int,
) {
    PlayerNameEmpty(
        R.string.error_title_player_name_empty,
        R.string.error_message_player_name_empty,
    ),
    DuplicateName(
        R.string.error_title_duplicate_name,
        R.string.error_message_duplicate_name,
    ),
    DuplicateIcon(
        R.string.error_title_duplicate_icon,
        R.string.error_message_duplicate_icon,
    ),
    DuplicateNameAndIcon(
        R.string.error_title_duplicate_name_icon,
        R.string.error_message_duplicate_name_icon
    ),
    DatabaseError(R.string.error_title_starting_game, R.string.error_message_starting_game),
}