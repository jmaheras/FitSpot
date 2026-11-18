package com.example.fitspot.data

import com.example.fitspot.models.ShoppingProduct

class ShoppingRepository {
    fun getProductRecommendations(label: String): List<ShoppingProduct> {
        return when (label) {
            "Denim Jacket" -> listOf(
                ShoppingProduct(
                    id = "p1",
                    title = "Levi's Trucker Denim Jacket",
                    retailer = "Levi's",
                    price = "$89.50",
                    imageUrl = "https://images.unsplash.com/photo-1576995853123-5a10305d93c0",
                    productUrl = "https://www.levi.com"
                ),
                ShoppingProduct(
                    id = "p2",
                    title = "Vintage Wash Denim Jacket",
                    retailer = "Gap",
                    price = "$69.99",
                    imageUrl = "https://images.unsplash.com/photo-1551028719-00167b16eac5",
                    productUrl = "https://www.gap.com"
                )
            )
            "White Cotton Tee" -> listOf(
                ShoppingProduct(
                    id = "p3",
                    title = "Essential Crewneck Cotton Tee",
                    retailer = "Uniqlo",
                    price = "$19.90",
                    imageUrl = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518",
                    productUrl = "https://www.uniqlo.com"
                )
            )
            else -> listOf(
                ShoppingProduct(
                    id = "p4",
                    title = "Retro Court Classic Sneakers",
                    retailer = "Adidas",
                    price = "$110.00",
                    imageUrl = "https://images.unsplash.com/photo-1549298916-b41d501d3772",
                    productUrl = "https://www.adidas.com"
                )
            )
        }
    }
}
