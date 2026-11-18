package com.example.fitspot.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fitspot.data.StoreRepository
import com.example.fitspot.models.RetailerStore
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val storeRepository = remember { StoreRepository() }
    val stores = remember { storeRepository.getNearbyStores() }

    var selectedStore by remember { mutableStateOf<RetailerStore?>(null) }
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var showListView by remember { mutableStateOf(false) }

    // Location Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        locationPermissionGranted = isGranted
    }

    // Default Camera position centered around Columbus, OH stores
    val defaultLocation = LatLng(40.05, -82.95)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 11f)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Header Bar
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📍 Nearby Retailers",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Discover local stores with apparel in stock",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    TextButton(onClick = { showListView = !showListView }) {
                        Text(if (showListView) "🗺️ Map View" else "📋 List View")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (!locationPermissionGranted) {
                    Button(
                        onClick = {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    ) {
                        Text("Enable Location Permissions")
                    }
                } else {
                    Text(
                        text = "GPS Location Active • Showing ${stores.size} Stores Nearby",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (showListView) {
            // Store List View
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(stores) { store ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = store.name, style = MaterialTheme.typography.titleMedium)
                            Text(text = store.address, style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Item: ${store.itemTitle}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Distance: ${store.distance}", style = MaterialTheme.typography.bodySmall)
                            Text(text = store.inStockStatus, color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    val mapUri = Uri.parse("geo:${store.latitude},${store.longitude}?q=${Uri.encode(store.name)}")
                                    val intent = Intent(Intent.ACTION_VIEW, mapUri)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("🚗 Get Directions")
                            }
                        }
                    }
                }
            }
        } else {
            // Google Map View
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = locationPermissionGranted)
                ) {
                    stores.forEach { store ->
                        Marker(
                            state = MarkerState(position = LatLng(store.latitude, store.longitude)),
                            title = store.name,
                            snippet = "${store.distance} • ${store.inStockStatus}",
                            onClick = {
                                selectedStore = store
                                true
                            }
                        )
                    }
                }

                // Store Details Pop-up / Dialog
                selectedStore?.let { store ->
                    AlertDialog(
                        onDismissRequest = { selectedStore = null },
                        title = { Text(text = store.name) },
                        text = {
                            Column {
                                Text(
                                    text = "Item: ${store.itemTitle}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = store.address,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Distance: ${store.distance}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Status: ${store.inStockStatus}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    val mapUri = Uri.parse("geo:${store.latitude},${store.longitude}?q=${Uri.encode(store.name)}")
                                    val intent = Intent(Intent.ACTION_VIEW, mapUri)
                                    context.startActivity(intent)
                                }
                            ) {
                                Text("🚗 Get Directions")
                            }
                        },
                        dismissButton = {
                            OutlinedButton(onClick = { selectedStore = null }) {
                                Text("Close")
                            }
                        }
                    )
                }
            }
        }
    }
}
