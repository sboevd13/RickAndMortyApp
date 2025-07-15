package com.java.testtask.ui.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.java.testtask.data.models.Character
import com.java.testtask.data.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CharacterFilters(
    val status: String = "",
    val gender: String = "",
    val species: String = ""
)

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val repository: CharacterRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filters = MutableStateFlow(CharacterFilters())
    val filters = _filters.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val characters: Flow<PagingData<Character>> =
        combine(_searchQuery, _filters) { query, currentFilters ->
            Pair(query, currentFilters)
        }.flatMapLatest { (query, currentFilters) ->
            repository.getCharacters(query, currentFilters)
        }.cachedIn(viewModelScope)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun applyFilters(newFilters: CharacterFilters) {
        _filters.value = newFilters
    }
}