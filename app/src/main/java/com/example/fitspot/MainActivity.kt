package com.example.fitspot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.fitspot.ui.theme.FitSpotTheme
import com.example.fitspot.ui.screens.LoginScreen
import com.example.fitspot.ui.screens.HomeScreen

import android.util.Log
private const val TAG = "FitSpotLifeCycle"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity: onCreate called ")
        enableEdgeToEdge()
        setContent{
            FitSpotTheme{
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var currentScreen by remember { mutableStateOf("login") }

                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            "login" -> LoginScreen(onLoginSuccess= { currentScreen = "home"})
                            "home" -> HomeScreen(onHomeScreenSuccess = {currentScreen = "login"})
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "MainActivity: onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity: onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "MainActivity: onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "MainActivity: onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity: onDestroy called")
    }
}
