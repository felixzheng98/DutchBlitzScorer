package com.zhengineer.dutchblitzscorer.common.result

import com.zhengineer.dutchblitzscorer.common.result.error.CreateGameError

sealed class CreateGameResult {
    data class Success(val gameId: Long) : CreateGameResult()
    data class Error(val error: CreateGameError, val throwable: Throwable? = null) :
        CreateGameResult()
}


