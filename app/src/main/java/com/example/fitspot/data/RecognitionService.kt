package com.example.fitspot.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import com.example.fitspot.BuildConfig
import com.example.fitspot.models.DetectedClothingItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

class RecognitionService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val cache = mutableMapOf<String, List<DetectedClothingItem>>()

    suspend fun scanImageForApparel(context: Context, imageUri: Uri): List<DetectedClothingItem> = withContext(Dispatchers.IO) {
        val cacheKey = imageUri.toString()
        if (cache.containsKey(cacheKey)) {
            Log.d("FitSpot", "Using cached AI recognition for $cacheKey")
            return@withContext cache[cacheKey]!!
        }

        // We added "gemini-1.5-flash" back to the front of the list, which will work perfectly with a brand new, standard personal API key
        val modelsToTry = listOf("gemini-3.5-flash", "gemini-1.5-flash", "gemini-2.5-flash", "gemini-3.7-flash")
        var lastErrorMsg = ""

        var lastErrorCode = -1

        val bitmap = getBitmapFromUri(context, imageUri)
        val resizedBitmap = resizeBitmap(bitmap, 512) // Resize to 512px to dramatically reduce upload size and prevent timeout
        val base64Image = encodeImageToBase64(resizedBitmap)

        val prompt = """
            Analyze this image and identify the primary clothing items worn. 
            Return ONLY a JSON array of objects, with no markdown formatting.
            Each object must have:
            - 'label' (String): A highly specific, retail-optimized e-commerce search query for Google Shopping to find this exact item or a very close match. Include gender (if obvious), color, material, pattern, style, and distinctive features (e.g., 'Mens vintage washed oversized blue denim trucker jacket' or 'Womens chunky white platform leather sneakers').
            - 'category' (String): Must be exactly one of: Jackets, Shirts, Pants, Footwear, Accessories, Other.
            - 'confidence' (Float): A number between 0.0 and 1.0 indicating your confidence.
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                        put(JSONObject().apply {
                            put("inlineData", JSONObject().apply {
                                put("mimeType", "image/jpeg")
                                put("data", base64Image)
                            })
                        })
                    })
                })
            })
            // Force JSON output
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
            })
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())

        for (modelName in modelsToTry) {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=${BuildConfig.GEMINI_API_KEY}"
                
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBodyStr = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    lastErrorCode = response.code
                    lastErrorMsg = "Model $modelName HTTP ${response.code}: $responseBodyStr"
                    Log.e("FitSpot", lastErrorMsg)
                    continue // Instantly skips to the next model without waiting!
                }

                val responseJson = JSONObject(responseBodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val content = candidates.getJSONObject(0).optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val textResponse = parts.getJSONObject(0).optString("text", "[]").trim()
                        
                        val jsonString = textResponse.replace("```json", "").replace("```", "").trim()
                        val jsonArray = try { JSONArray(jsonString) } catch (e: Exception) { JSONArray("[]") }
                        
                        val items = mutableListOf<DetectedClothingItem>()
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            items.add(
                                DetectedClothingItem(
                                    id = UUID.randomUUID().toString(),
                                    label = obj.optString("label", "Unknown Item"),
                                    category = obj.optString("category", "Other"),
                                    confidence = obj.optDouble("confidence", 0.0).toFloat()
                                )
                            )
                        }
                        
                        if (items.isNotEmpty()) {
                            cache[cacheKey] = items
                            return@withContext items
                        }
                    }
                }
                
                lastErrorMsg = "Model $modelName returned invalid format."
                
            } catch (e: Exception) {
                e.printStackTrace()
                lastErrorMsg = e.message ?: "Unknown network error"
            }
        }
        
        val userFriendlyMessage = when {
            lastErrorCode == 503 -> "Server Error (503): The AI models are currently overwhelmed and busy. Please wait a few minutes and try again."
            lastErrorCode == 429 -> "Quota Error (429): Your Google AI API key has run out of tokens or hit its rate limit. Please use a fresh API key or wait for the quota to reset."
            lastErrorCode == 400 -> "Auth Error (400): The API key provided in local.properties is invalid."
            lastErrorCode == 402 -> "Billing Error (402): The Google account linked to this API key has depleted its credits. Please generate a new key."
            lastErrorCode == 404 -> "Model Error (404): The specified Gemini model is not available for this API key."
            lastErrorMsg.contains("timeout", ignoreCase = true) -> "Network Error: The connection timed out while uploading the image to Google. Please check your internet connection and try again."
            else -> "Failed to identify apparel. Reason: $lastErrorMsg"
        }
        
        Log.e("FitSpot", userFriendlyMessage)
        throw Exception(userFriendlyMessage)
    }

    private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        
        if (width <= maxSize && height <= maxSize) return bitmap

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
    
    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}