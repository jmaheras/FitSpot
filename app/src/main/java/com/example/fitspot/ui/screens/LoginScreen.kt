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
    fun LoginScreen(onLoginSuccess: () -> Unit) {
        DisposableEffect(Unit) {
            Log.d("FitSpotLifeCycle", "LoginScreen: Entered Screen (Composition)")
            onDispose {
                Log.d("FitSpotLifeCycle", "LoginScreen: Exited Screen (Disposed)")
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "FitSpot - Login")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLoginSuccess) {
                Text(text = "login")
            }
        }
    }
