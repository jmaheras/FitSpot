package com.example.fitspot.viewmodel

import androidx.lifecycle.ViewModel
import com.example.fitspot.data.RetailerStoreRepository
import com.example.fitspot.models.RetailerStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MapUiState(
    val stores: List<RetailerStore> = emptyList(),
    val selectedStore: RetailerStore? = null,
    val showListView: Boolean = false,
    val locationPermissionGranted: Boolean = false
)

class MapViewModel(
    private val storeRepository: RetailerStoreRepository = RetailerStoreRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState(stores = storeRepository.getNearbyStores()))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    fun setLocationPermissionGranted(granted: Boolean) {
        _uiState.update { it.copy(locationPermissionGranted = granted) }
    }

    fun toggleViewMode() {
        _uiState.update { it.copy(showListView = !it.showListView) }
    }

    fun selectStore(store: RetailerStore?) {
        _uiState.update { it.copy(selectedStore = store) }
    }
}
