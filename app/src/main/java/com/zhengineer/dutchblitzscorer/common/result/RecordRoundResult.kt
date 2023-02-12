package com.zhengineer.dutchblitzscorer.common.result

import com.zhengineer.dutchblitzscorer.common.result.error.RecordRoundError

sealed class RecordRoundResult {
    object Success : RecordRoundResult()
    class Error(val error: RecordRoundError, val throwable: Throwable? = null) : RecordRoundResult()
}