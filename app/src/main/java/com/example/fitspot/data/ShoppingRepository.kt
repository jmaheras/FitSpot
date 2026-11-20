package com.example.fitspot.data

import android.net.Uri
import com.example.fitspot.BuildConfig
import com.example.fitspot.models.ShoppingProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.UUID

class ShoppingRepository {
    private val client = OkHttpClient()
    private val cache = mutableMapOf<String, List<ShoppingProduct>>()

    suspend fun getProductRecommendations(label: String): List<ShoppingProduct> = withContext(Dispatchers.IO) {
        val normalizedLabel = label.trim().lowercase()
        if (cache.containsKey(normalizedLabel)) {
            return@withContext cache[normalizedLabel]!!
        }

        try {
            val encodedLabel = Uri.encode(label)
            val url = "https://serpapi.com/search.json?engine=google_shopping&q=$encodedLabel&api_key=${BuildConfig.SERPAPI_KEY}"
            
            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val jsonObject = JSONObject(responseBody)
                val shoppingResults = jsonObject.optJSONArray("shopping_results") ?: return@withContext emptyList()
                
                val products = mutableListOf<ShoppingProduct>()
                val maxItems = minOf(10, shoppingResults.length()) // Limit to top 10
                
                for (i in 0 until maxItems) {
                    val item = shoppingResults.getJSONObject(i)
                    products.add(
                        ShoppingProduct(
                            id = UUID.randomUUID().toString(),
                            title = item.optString("title", "Unknown Title"),
                            retailer = item.optString("source", "Unknown Retailer"),
                            price = item.optString("price", "Price N/A"),
                            imageUrl = item.optString("thumbnail", ""),
                            productUrl = item.optString("product_link", "")
                        )
                    )
                }
                
                // Save to cache before returning
                cache[normalizedLabel] = products
                products
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
