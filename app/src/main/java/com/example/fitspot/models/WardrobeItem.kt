package com.example.fitspot.models

import com.google.firebase.firestore.DocumentId

data class WardrobeItem(
    @DocumentId val id: String = "",
    val title: String = "",
    val category: String = "",
    val retailer: String = "",
    val price: String = ""
)