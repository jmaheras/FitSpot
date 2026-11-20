package com.example.fitspot.data

import android.location.Location
import com.example.fitspot.models.RetailerStore
import com.example.fitspot.models.StockStatus
import com.example.fitspot.models.Store

// === Teammate's StoreRepository interface and Mock implementation ===
interface StoreRepository {
    suspend fun nearbyStores(latitude: Double, longitude: Double): List<Store>
}

class MockStoreRepository : StoreRepository {
    private data class Seed(val name: String, val dLat: Double, val dLng: Double, val stock: StockStatus)

    private val seeds = listOf(
        Seed("Zara", 0.0040, 0.0030, StockStatus.IN_STOCK),
        Seed("Uniqlo", -0.0050, 0.0040, StockStatus.IN_STOCK),
        Seed("H&M", 0.0070, -0.0050, StockStatus.LOW_STOCK),
        Seed("Nike", -0.0030, -0.0080, StockStatus.IN_STOCK),
        Seed("Levi's", 0.0100, 0.0070, StockStatus.OUT_OF_STOCK),
        Seed("Nordstrom Rack", -0.0090, -0.0030, StockStatus.LOW_STOCK)
    )

    override suspend fun nearbyStores(latitude: Double, longitude: Double): List<Store> =
        seeds.mapIndexed { index, s ->
            val lat = latitude + s.dLat
            val lng = longitude + s.dLng
            val result = FloatArray(1)
            Location.distanceBetween(latitude, longitude, lat, lng, result)
            Store("mock-$index", s.name, lat, lng, result[0], s.stock)
        }.sortedBy { it.distanceMeters }
}

// === My original class renamed for my MapViewModel to compile ===
class RetailerStoreRepository {
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
