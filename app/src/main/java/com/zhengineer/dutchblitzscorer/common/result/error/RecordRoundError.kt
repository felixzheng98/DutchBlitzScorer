package com.zhengineer.dutchblitzscorer.common.result.error

import androidx.annotation.StringRes
import com.zhengineer.dutchblitzscorer.R

enum class RecordRoundError(
    @StringRes val errorDialogTitle: Int,
    @StringRes val errorDialogMessage: Int,
) {
    INVALID_INPUT(R.string.error_title_score_empty, R.string.error_message_score_empty),
    DATABASE_ERROR(R.string.error_title_record_round, R.string.error_message_record_round),
}