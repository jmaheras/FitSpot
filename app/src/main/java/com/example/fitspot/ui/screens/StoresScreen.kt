package com.example.fitspot.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitspot.models.StockStatus
import com.example.fitspot.models.Store
import com.example.fitspot.viewmodel.StoresViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

private fun hasLocationPermission(context: Context) =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED

@Composable
fun StoresScreen(viewModel: StoresViewModel = viewModel()) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(39.9992, -83.0129), 12f) // Columbus, OH fallback
    }

    @SuppressLint("MissingPermission") // guarded by hasLocationPermission() at every call site
    fun fetchLocationAndStores() {
        viewModel.setLoading()
        scope.launch {
            try {
                val client = LocationServices.getFusedLocationProviderClient(context)
                val loc = client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
                    ?: client.lastLocation.await()
                if (loc == null) {
                    viewModel.setError("Couldn't get your location. Check that GPS is on and try again.")
                } else {
                    viewModel.loadStores(loc.latitude, loc.longitude)
                }
            } catch (e: Exception) {
                viewModel.setError(e.message ?: "Couldn't get your location")
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.any { it }) fetchLocationAndStores()
        else viewModel.setError("Location permission is needed to find stores near you.")
    }

    // Recenter on the user once stores are loaded
    LaunchedEffect(state.userLat, state.userLng) {
        val lat = state.userLat
        val lng = state.userLng
        if (lat != null && lng != null) {
            cameraState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 14f))
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Nearby Stores", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                if (hasLocationPermission(context)) fetchLocationAndStores()
                else permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) CircularProgressIndicator(Modifier.height(20.dp), strokeWidth = 2.dp)
            else Text("Find Nearby Stores")
        }

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(12.dp))

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission(context)),
                onMapClick = { viewModel.select(null) }
            ) {
                state.stores.forEach { store ->
                    Marker(
                        state = MarkerState(LatLng(store.latitude, store.longitude)),
                        title = store.name,
                        onClick = { viewModel.select(store); false }
                    )
                }
            }
        }

        state.selected?.let { store ->
            Spacer(Modifier.height(12.dp))
            StoreDetailsCard(store = store, onDirections = { openDirections(context, store) })
        }

        if (state.stores.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 150.dp)) {
                items(state.stores, key = { it.id }) { store ->
                    StoreRow(
                        store = store,
                        selected = store.id == state.selected?.id,
                        onClick = {
                            viewModel.select(store)
                            scope.launch {
                                cameraState.animate(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(store.latitude, store.longitude), 15f
                                    )
                                )
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun StoreRow(store: Store, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (selected) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.surface
            )
            .padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(store.name, style = MaterialTheme.typography.bodyLarge)
        Text(formatDistance(store.distanceMeters), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun StoreDetailsCard(store: Store, onDirections: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(store.name, style = MaterialTheme.typography.titleLarge)
            Text(formatDistance(store.distanceMeters), style = MaterialTheme.typography.bodyMedium)
            Text(
                "Estimated: ${store.stock.label}",
                style = MaterialTheme.typography.bodyMedium,
                color = when (store.stock) {
                    StockStatus.IN_STOCK -> MaterialTheme.colorScheme.primary
                    StockStatus.LOW_STOCK -> MaterialTheme.colorScheme.tertiary
                    StockStatus.OUT_OF_STOCK -> MaterialTheme.colorScheme.error
                }
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = onDirections, modifier = Modifier.fillMaxWidth()) {
                Text("Get Directions")
            }
        }
    }
}

private fun formatDistance(meters: Float): String =
    if (meters < 1000f) "${meters.toInt()} m away" else "%.1f km away".format(meters / 1000f)

private fun openDirections(context: Context, store: Store) {
    val navIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("google.navigation:q=${store.latitude},${store.longitude}")
    )
    val webIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(
            "https://www.google.com/maps/dir/?api=1&destination=${store.latitude},${store.longitude}"
        )
    )
    try {
        context.startActivity(navIntent)
    } catch (e: ActivityNotFoundException) {
        try {
            context.startActivity(webIntent)
        } catch (e2: ActivityNotFoundException) {
            Toast.makeText(context, "No maps app available", Toast.LENGTH_SHORT).show()
        }
    }
}
