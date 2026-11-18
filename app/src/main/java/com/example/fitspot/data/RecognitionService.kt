package com.example.fitspot.data

import android.net.Uri
import com.example.fitspot.models.DetectedClothingItem

class RecognitionService {
    fun scanImageForApparel(imageUri: Uri?): List<DetectedClothingItem> {
        return listOf(
            DetectedClothingItem(id = "1", label = "Denim Jacket", category = "Jackets", confidence = 0.98f),
            DetectedClothingItem(id = "2", label = "White Cotton Tee", category = "Shirts", confidence = 0.94f),
            DetectedClothingItem(id = "3", label = "Classic Sneakers", category = "Footwear", confidence = 0.91f)
        )
    }
}
