package com.example.fitspot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitspot.data.WardrobeRepository
import com.example.fitspot.models.WardrobeItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WardrobeViewModel : ViewModel() {
    private val repository = WardrobeRepository()

    private val _wardrobeItems = MutableStateFlow<List<WardrobeItem>>(emptyList())
    val wardrobeItems: StateFlow<List<WardrobeItem>> = _wardrobeItems.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getWardrobeItems().collect { items ->
                _wardrobeItems.value = items
            }
        }
    }

    fun addItem(item: WardrobeItem) = repository.addItem(item)
    fun updateItem(item: WardrobeItem) = repository.updateItem(item)
    fun deleteItem(itemId: String) = repository.deleteItem(itemId)
}