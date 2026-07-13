package com.szam.cary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.szam.cary.data.model.FoundItem
import com.szam.cary.data.model.ItemKategori
import com.szam.cary.data.model.ItemStatus
import com.szam.cary.data.model.LostItem
import com.szam.cary.data.repository.CaryRepository
import com.szam.cary.data.repository.FirebaseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val isLoading: Boolean = false,
    val query: String = "",
    val selectedKategori: ItemKategori? = null,
    val selectedStatus: ItemStatus? = null,
    val lostItems: List<LostItem> = emptyList(),
    val foundItems: List<FoundItem> = emptyList(),
    val error: String? = null
)

class SearchViewModel(private val repository: CaryRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        debounceSearch()
    }

    fun updateKategori(kategori: ItemKategori?) {
        _uiState.value = _uiState.value.copy(selectedKategori = kategori)
        search()
    }

    fun updateStatus(status: ItemStatus?) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)
        search()
    }

    private fun debounceSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
            search()
        }
    }

    fun search() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val (lost, found) = repository.searchItems(
                    query = _uiState.value.query,
                    kategori = _uiState.value.selectedKategori,
                    status = _uiState.value.selectedStatus
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lostItems = lost,
                    foundItems = found
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            query = "",
            selectedKategori = null,
            selectedStatus = null
        )
        search()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(FirebaseRepository()) as T
            }
        }
    }
}
