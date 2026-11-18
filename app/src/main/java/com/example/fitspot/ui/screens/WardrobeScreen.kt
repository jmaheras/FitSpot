package com.example.fitspot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitspot.models.WardrobeItem
import com.example.fitspot.viewmodel.WardrobeViewModel

@Composable
fun WardrobeScreen(
    viewModel: WardrobeViewModel = viewModel()
) {
    val wardrobeItems by viewModel.wardrobeItems.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Digital Wardrobe",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Add Mock Item Button
        Button(
            onClick = {
                val newItem = WardrobeItem(
                    title = "Saved Jacket",
                    category = "Jackets",
                    retailer = "FitSpot Store",
                    price = "$59.99"
                )
                viewModel.addItem(newItem)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("➕ Add Item to Digital Wardrobe")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (wardrobeItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Your wardrobe is empty. Add items or scan outfits!")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize()
            ) {
                items(wardrobeItems) { item ->
                    WardrobeCard(
                        item = item,
                        onDelete = { viewModel.deleteItem(item.id) },
                        onEditPrice = { newPrice -> viewModel.updateItemPrice(item.id, newPrice) }
                    )
                }
            }
        }
    }
}
