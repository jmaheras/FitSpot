package com.example.fitspot.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitspot.data.RecognitionService
import com.example.fitspot.data.ShoppingRepository
import com.example.fitspot.models.DetectedClothingItem
import com.example.fitspot.models.ShoppingProduct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ScanUiState(
    val imageUri: Uri? = null,
    val detectedItems: List<DetectedClothingItem> = emptyList(),
    val selectedTag: String? = null,
    val products: List<ShoppingProduct> = emptyList(),
    val notificationMsg: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ScanViewModel(
    private val recognitionService: RecognitionService = RecognitionService(),
    private val shoppingRepository: ShoppingRepository = ShoppingRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun onImageSelected(uri: Uri?) {
        _uiState.update { 
            it.copy(
                imageUri = uri,
                detectedItems = emptyList(),
                selectedTag = null,
                products = emptyList(),
                notificationMsg = null,
                isLoading = false,
                error = null
            )
        }
    }

    fun scanImage(context: Context) {
        val currentUri = _uiState.value.imageUri ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val items = recognitionService.scanImageForApparel(context, currentUri)
                
                if (items.isEmpty()) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = "No clothing items detected or error occurred.",
                            detectedItems = emptyList()
                        ) 
                    }
                    return@launch
                }
                
                _uiState.update { it.copy(detectedItems = items) }
                
                // Automatically fetch recommendations for the first tag
                val firstTag = items.first().label
                fetchRecommendations(firstTag)
                
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error: ${e.message}") }
            }
        }
    }

    fun onTagSelected(tag: String) {
        viewModelScope.launch {
            fetchRecommendations(tag)
        }
    }

    private suspend fun fetchRecommendations(tag: String) {
        _uiState.update { it.copy(selectedTag = tag, isLoading = true, error = null) }
        try {
            val products = shoppingRepository.getProductRecommendations(tag)
            _uiState.update { 
                it.copy(
                    products = products,
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = "Failed to fetch recommendations.") }
        }
    }

    fun setNotification(msg: String?) {
        _uiState.update { it.copy(notificationMsg = msg) }
    }
}
