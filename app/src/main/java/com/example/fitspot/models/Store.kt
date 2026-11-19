package com.example.fitspot.models

enum class StockStatus(val label: String) {
    IN_STOCK("In stock"),
    LOW_STOCK("Low stock"),
    OUT_OF_STOCK("Out of stock")
}

data class Store(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Float,
    val stock: StockStatus
)
