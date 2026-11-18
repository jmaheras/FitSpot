package com.example.fitspot.models

data class DetectedClothingItem(
    val id: String = "",
    val label: String = "",
    val category: String = "",
    val confidence: Float = 0.95f
)

data class ShoppingProduct(
    val id: String = "",
    val title: String = "",
    val retailer: String = "",
    val price: String = "",
    val imageUrl: String = "",
    val productUrl: String = ""
)
