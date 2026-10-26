package com.example.fitspot.ui.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitspot.models.WardrobeItem
import com.example.fitspot.viewmodel.WardrobeViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

@Composable
fun HomeScreen(
    viewModel: WardrobeViewModel = viewModel(),
    onHomeScreenSuccess: () -> Unit
) {
    DisposableEffect(Unit) {
        Log.d("FitSpotLifeCycle", "HomeScreen: Entered Screen (Composition)")
        onDispose {
            Log.d("FitSpotLifeCycle", "HomeScreen: Exited Screen (Disposed)")
        }
    }

    var selectedTab by remember { mutableStateOf(0) }

    // Observe live Firebase data instead of the hardcoded list
    val wardrobeItems by viewModel.wardrobeItems.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Home") },
                    icon = { Text("🏠") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Wardrobe") },
                    icon = { Text("👕") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        onHomeScreenSuccess()
                    },
                    label = { Text("Profile") },
                    icon = { Text("👤") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "FitSpot",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Create Operation Trigger
            Button(
                onClick = {
                    val testItem = WardrobeItem(
                        title = "Test Jacket",
                        category = "Jackets",
                        retailer = "TestBrand",
                        price = "$49.99"
                    )
                    viewModel.addItem(testItem)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "➕ Add Mock Item to Database",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Recent Scans",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Read Operation Display
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize()
            ) {
                items(wardrobeItems) { item ->
                    WardrobeCard(item = item, onDelete = { viewModel.deleteItem(item.id) })
                }
            }
        }
    }
}

@Composable
fun WardrobeCard(item: WardrobeItem, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = item.category.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${item.retailer}\n${item.price}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Delete Operation Trigger
            Button(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
                Text("Delete")
            }
        }
    }
}