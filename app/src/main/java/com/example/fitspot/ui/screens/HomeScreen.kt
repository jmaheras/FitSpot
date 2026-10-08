package com.example.fitspot.ui.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun HomeScreen(onHomeScreenSuccess: () -> Unit) {
    DisposableEffect(Unit) {
        Log.d("FitSpotLifeCycle", "HomeScreen: Entered Screen (Composition)")
        onDispose {
            Log.d("FitSpotLifeCycle", "HomeScreen: Exited Screen (Disposed)")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "FitSpot - Login")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onHomeScreenSuccess) {
            Text(text = "HomeScreen Success")
        }
    }
}
