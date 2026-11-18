package com.example.fitspot.data

import com.example.fitspot.models.RetailerStore

class StoreRepository {
    fun getNearbyStores(): List<RetailerStore> {
        return listOf(
            RetailerStore(
                id = "s1",
                name = "Levi's Store - Easton Town Center",
                address = "4115 Easton Town Center, Columbus, OH 43219",
                latitude = 40.0505,
                longitude = -82.9168,
                distance = "1.2 miles away",
                inStockStatus = "In Stock (3 items available)",
                itemTitle = "Levi's Trucker Denim Jacket"
            ),
            RetailerStore(
                id = "s2",
                name = "Nordstrom - Easton",
                address = "4100 Easton Loop W, Columbus, OH 43219",
                latitude = 40.0518,
                longitude = -82.9185,
                distance = "1.4 miles away",
                inStockStatus = "In Stock (Limited Quantity)",
                itemTitle = "Retro Court Sneakers"
            ),
            RetailerStore(
                id = "s3",
                name = "Gap - Polaris Fashion Place",
                address = "1500 Polaris Pkwy, Columbus, OH 43240",
                latitude = 40.1460,
                longitude = -82.9800,
                distance = "5.8 miles away",
                inStockStatus = "In Stock (5 items available)",
                itemTitle = "Vintage Wash Denim Jacket"
            ),
            RetailerStore(
                id = "s4",
                name = "Uniqlo - City Center",
                address = "120 S High St, Columbus, OH 43215",
                latitude = 39.9598,
                longitude = -82.9990,
                distance = "8.1 miles away",
                inStockStatus = "In Stock (In Many Sizes)",
                itemTitle = "Essential Crewneck Cotton Tee"
            )
        )
    }
}
