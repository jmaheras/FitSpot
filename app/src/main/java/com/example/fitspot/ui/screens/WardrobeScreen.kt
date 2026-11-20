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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen(
    viewModel: WardrobeViewModel = viewModel()
) {
    val wardrobeItems by viewModel.wardrobeItems.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("Jackets", "Shirts", "Pants", "Footwear", "Accessories", "Other")

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf(categories[0]) }
        var retailer by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var expanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Wardrobe Item") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it; errorMessage = null },
                        label = { Text("Title *") },
                        singleLine = true
                    )
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        category = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = retailer,
                        onValueChange = { retailer = it },
                        label = { Text("Retailer") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it; errorMessage = null },
                        label = { Text("Price *") },
                        singleLine = true
                    )
                    if (errorMessage != null) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isBlank() || price.isBlank()) {
                            errorMessage = "Title and Price cannot be blank."
                        } else {
                            viewModel.addItem(
                                WardrobeItem(
                                    title = title.trim(),
                                    category = category,
                                    retailer = retailer.trim(),
                                    price = price.trim()
                                )
                            )
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

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

        // Add Item Button
        Button(
            onClick = { showAddDialog = true },
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
