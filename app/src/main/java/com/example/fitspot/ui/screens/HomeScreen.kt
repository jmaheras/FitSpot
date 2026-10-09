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
import com.example.fitspot.ui.models.WardrobeItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold


@Composable
fun HomeScreen(onHomeScreenSuccess: () -> Unit) {
    DisposableEffect(Unit) {
        Log.d("FitSpotLifeCycle", "HomeScreen: Entered Screen (Composition)")
        onDispose {
            Log.d("FitSpotLifeCycle", "HomeScreen: Exited Screen (Disposed)")
        }
    }

    var selectedTab by remember { mutableStateOf(0) }

    // Sample Recent Scans
    val recentScans = remember {
        listOf(
            WardrobeItem("1", "Denim Jacket", "Jackets", "Levi's", "$89.99"),
            WardrobeItem("2", "White Sneakers", "Footwear", "Nike", "$110.00"),
            WardrobeItem("3", "Leather Bag", "Accessories", "Coach", "$195.00"),
            WardrobeItem("4", "Graphic Tee", "Shirts", "Uniqlo", "$24.99")
        )
    }

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
                        onHomeScreenSuccess() // Log out when Profile/Log out clicked
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
            // Header Title
            Text(
                text = "FitSpot",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 📸 Hero Action: Scan an Outfit
            Button(
                onClick = { /* TODO: Open Camera / Image Scan */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "📷  Scan an Outfit",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🕒 Recent Scans Section Header
            Text(
                text = "Recent Scans",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Recent Scans Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize()
            ) {
                items(recentScans) { item ->
                    WardrobeCard(item = item)
                }
            }
        }
    }
}

@Composable
fun WardrobeCard(item: WardrobeItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        )
        {
            Text(
                text = item.category.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${item.retailer} * ${item.price}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}



