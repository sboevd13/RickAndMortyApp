package com.java.testtask.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.java.testtask.data.models.Character
import com.java.testtask.data.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DetailUiState {
    data class Success(val character: Character) : DetailUiState
    object Error : DetailUiState
    object Loading : DetailUiState
}

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val repository: CharacterRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        val characterId: Int? = savedStateHandle["id"]
        if (characterId != null) {
            fetchCharacterDetails(characterId)
        } else {
            _uiState.value = DetailUiState.Error
        }
    }

    private fun fetchCharacterDetails(id: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            repository.getCharacterDetails(id)
                .onSuccess { character ->
                    _uiState.value = DetailUiState.Success(character)
                }
                .onFailure {
                    _uiState.value = DetailUiState.Error
                }
        }
    }
}