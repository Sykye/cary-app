package com.szam.cary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.szam.cary.data.model.FoundItem
import com.szam.cary.data.repository.CaryRepository
import com.szam.cary.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FoundItemUiState(
    val isLoading: Boolean = false,
    val items: List<FoundItem> = emptyList(),
    val selectedItem: FoundItem? = null,
    val error: String? = null,
    val successMessage: String? = null
)

class FoundItemViewModel(private val repository: CaryRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FoundItemUiState())
    val uiState: StateFlow<FoundItemUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val items = repository.getFoundItems()
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
                val item = repository.getFoundItemById(id)
                _uiState.value = _uiState.value.copy(isLoading = false, selectedItem = item)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun addItem(item: FoundItem) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.addFoundItem(item)
            result.fold(
                onSuccess = {
                    loadItems()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Laporan barang ditemukan berhasil ditambahkan"
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
            )
        }
    }

    fun updateItem(item: FoundItem) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.updateFoundItem(item)
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
            val result = repository.deleteFoundItem(id)
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
                return FoundItemViewModel(FirebaseRepository()) as T
            }
        }
    }
}
