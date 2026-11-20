package com.example.fitspot.models

data class RetailerStore(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val distance: String = "",
    val inStockStatus: String = "",
    val itemTitle: String = ""
)
