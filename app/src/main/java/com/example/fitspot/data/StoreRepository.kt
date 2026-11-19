package com.example.fitspot.data

import android.location.Location
import com.example.fitspot.models.StockStatus
import com.example.fitspot.models.Store

interface StoreRepository {
    suspend fun nearbyStores(latitude: Double, longitude: Double): List<Store>
}

/**
 * Placeholder until a real retailer/inventory API is wired in. Generates a fixed set of
 * well-known clothing retailers at deterministic offsets (roughly 200m-1.5km) around the
 * user's position, so the map and detail flow can be demonstrated anywhere.
 */
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
