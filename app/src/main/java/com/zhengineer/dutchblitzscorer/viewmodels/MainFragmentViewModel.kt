package com.zhengineer.dutchblitzscorer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.zhengineer.dutchblitzscorer.database.multitable.GameInfo
import com.zhengineer.dutchblitzscorer.database.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val gameRepository: GameRepository
): ViewModel() {
    private val itemsSelected = MutableLiveData(false)

    val games: LiveData<List<GameInfo>> = gameRepository.getGameInfos().asLiveData()
    val itemsSelectedDistinct = Transformations.distinctUntilChanged(itemsSelected)
    val itemSelectionCount = MutableLiveData(0)

    fun markGamesDeleted(gameIds: List<Long>) {
        viewModelScope.launch {
            gameRepository.markDeleted(gameIds)
        }
        resetSelection()
    }

    fun updateSelection(size: Int) {
        itemsSelected.postValue(size > 0)
        itemSelectionCount.postValue(size)
    }

    fun resetSelection() {
        itemsSelected.value = false
        itemSelectionCount.value = 0
    }
}