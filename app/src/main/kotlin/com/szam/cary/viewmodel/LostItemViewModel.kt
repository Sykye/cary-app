package com.szam.cary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.szam.cary.data.model.FoundItem
import com.szam.cary.data.model.LostItem
import com.szam.cary.data.repository.CaryRepository
import com.szam.cary.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LostItemUiState(
    val isLoading: Boolean = false,
    val items: List<LostItem> = emptyList(),
    val selectedItem: LostItem? = null,
    val matchSuggestions: List<FoundItem> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class LostItemViewModel(private val repository: CaryRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LostItemUiState())
    val uiState: StateFlow<LostItemUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val items = repository.getLostItems()
                _uiState.value = _uiState.value.copy(isLoading = false, items = items)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun loadItemById(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val item = repository.getLostItemById(id)
                _uiState.value = _uiState.value.copy(isLoading = false, selectedItem = item)
                item?.let { loadMatchSuggestions(it) }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    private fun loadMatchSuggestions(item: LostItem) {
        viewModelScope.launch {
            try {
                val matches = repository.getMatchSuggestions(item)
                _uiState.value = _uiState.value.copy(matchSuggestions = matches)
            } catch (e: Exception) {
                // silently fail for suggestions
            }
        }
    }

    fun addItem(item: LostItem) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.addLostItem(item)
            result.fold(
                onSuccess = {
                    loadItems()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Laporan barang hilang berhasil ditambahkan"
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
            )
        }
    }

    fun updateItem(item: LostItem) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.updateLostItem(item)
            result.fold(
                onSuccess = {
                    loadItems()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Laporan berhasil diperbarui"
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
            )
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.deleteLostItem(id)
            result.fold(
                onSuccess = {
                    loadItems()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Laporan berhasil dihapus"
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LostItemViewModel(FirebaseRepository()) as T
            }
        }
    }
}
