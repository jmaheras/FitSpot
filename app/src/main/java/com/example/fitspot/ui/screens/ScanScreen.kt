package com.example.fitspot.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.fitspot.data.RecognitionService
import com.example.fitspot.data.ShoppingRepository
import com.example.fitspot.models.DetectedClothingItem
import com.example.fitspot.models.ShoppingProduct
import com.example.fitspot.models.WardrobeItem
import com.example.fitspot.viewmodel.WardrobeViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    viewModel: WardrobeViewModel = viewModel()
) {
    val context = LocalContext.current
    val recognitionService = remember { RecognitionService() }
    val shoppingRepository = remember { ShoppingRepository() }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var detectedItems by remember { mutableStateOf<List<DetectedClothingItem>>(emptyList()) }
    var selectedTag by remember { mutableStateOf<String?>(null) }
    var products by remember { mutableStateOf<List<ShoppingProduct>>(emptyList()) }
    var notificationMsg by remember { mutableStateOf<String?>(null) }

    // Gallery Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
            detectedItems = emptyList()
            selectedTag = null
            products = emptyList()
            notificationMsg = null
        }
    }

    // Camera Capture Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            imageUri = tempCameraUri
            detectedItems = emptyList()
            selectedTag = null
            products = emptyList()
            notificationMsg = null
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val tempFile = File.createTempFile("captured_outfit_", ".jpg", context.cacheDir).apply {
                createNewFile()
                deleteOnExit()
            }
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            )
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📷 Outfit Scanner & Shopping",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Image Preview or Placeholder
        if (imageUri != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Scanned Outfit",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Take a photo or upload an image to scan",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                modifier = Modifier.weight(1f)
            ) {
                Text("📷 Camera")
            }

            OutlinedButton(
                onClick = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("🖼️ Gallery")
            }
        }

        if (imageUri != null) {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val items = recognitionService.scanImageForApparel(imageUri)
                    detectedItems = items
                    if (items.isNotEmpty()) {
                        selectedTag = items.first().label
                        products = shoppingRepository.getProductRecommendations(items.first().label)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔍 Scan & Identify Apparel")
            }
        }

        // Display Identified Clothing Tags
        if (detectedItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Detected Apparel Pieces:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(detectedItems) { item ->
                    FilterChip(
                        selected = selectedTag == item.label,
                        onClick = {
                            selectedTag = item.label
                            products = shoppingRepository.getProductRecommendations(item.label)
                        },
                        label = { Text("${item.label} (${(item.confidence * 100).toInt()}%)") }
                    )
                }
            }
        }

        // Display Shopping Recommendations
        if (products.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Product Recommendations for \"$selectedTag\":",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            products.forEach { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = product.title, style = MaterialTheme.typography.titleMedium)
                        Text(text = "${product.retailer} • ${product.price}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.productUrl))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("🛍️ Shop Now")
                            }

                            OutlinedButton(
                                onClick = {
                                    val newItem = WardrobeItem(
                                        title = product.title,
                                        category = selectedTag ?: "Apparel",
                                        retailer = product.retailer,
                                        price = product.price,
                                        imageUrl = product.imageUrl
                                    )
                                    viewModel.addItem(newItem)
                                    notificationMsg = "Saved \"${product.title}\" to Wardrobe!"
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("💾 Save")
                            }
                        }
                    }
                }
            }

            notificationMsg?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = msg, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
