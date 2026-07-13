package com.szam.cary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.szam.cary.data.model.Claim
import com.szam.cary.data.model.ClaimStatus
import com.szam.cary.data.repository.CaryRepository
import com.szam.cary.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ClaimUiState(
    val isLoading: Boolean = false,
    val claims: List<Claim> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class ClaimViewModel(private val repository: CaryRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ClaimUiState())
    val uiState: StateFlow<ClaimUiState> = _uiState.asStateFlow()

    fun loadClaimsByUser(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val claims = repository.getClaimsByUser(userId)
                _uiState.value = _uiState.value.copy(isLoading = false, claims = claims)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun submitClaim(claim: Claim) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.addClaim(claim)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Klaim berhasil diajukan! Tunggu konfirmasi dari penemu."
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
            )
        }
    }

    fun updateClaimStatus(claimId: String, status: ClaimStatus) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.updateClaimStatus(claimId, status)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Status klaim berhasil diperbarui"
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
                return ClaimViewModel(FirebaseRepository()) as T
            }
        }
    }
}
