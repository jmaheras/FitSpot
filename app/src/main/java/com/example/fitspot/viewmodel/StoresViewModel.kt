package com.example.fitspot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitspot.data.MockStoreRepository
import com.example.fitspot.data.StoreRepository
import com.example.fitspot.models.Store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StoresUiState(
    val isLoading: Boolean = false,
    val userLat: Double? = null,
    val userLng: Double? = null,
    val stores: List<Store> = emptyList(),
    val selected: Store? = null,
    val error: String? = null
)

class StoresViewModel(
    private val repository: StoreRepository = MockStoreRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(StoresUiState())
    val uiState: StateFlow<StoresUiState> = _uiState.asStateFlow()

    fun setLoading() = _uiState.update { it.copy(isLoading = true, error = null) }

    fun setError(message: String) =
        _uiState.update { it.copy(isLoading = false, error = message) }

    fun loadStores(lat: Double, lng: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.nearbyStores(lat, lng) }
                .onSuccess { stores ->
                    _uiState.value = StoresUiState(userLat = lat, userLng = lng, stores = stores)
                }
                .onFailure { e ->
                    setError(e.message ?: "Could not load nearby stores")
                }
        }
    }

    fun select(store: Store?) = _uiState.update { it.copy(selected = store) }
}
