package com.example.fitspot.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitspot.ui.screens.CropScreen
import com.example.fitspot.ui.screens.HomeScreen
import com.example.fitspot.ui.screens.LoginScreen
import com.example.fitspot.ui.screens.MapScreen
import com.example.fitspot.ui.screens.ProfileScreen
import com.example.fitspot.ui.screens.ScanScreen
import com.example.fitspot.ui.screens.StoresScreen
import com.example.fitspot.ui.screens.WardrobeScreen
import com.example.fitspot.viewmodel.AuthViewModel
import com.example.fitspot.viewmodel.WardrobeViewModel

@Composable
fun FitSpotNavHost(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    wardrobeViewModel: WardrobeViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    val resetStatus by authViewModel.resetStatus.collectAsState()

    if (!authState.isLoggedIn) {
        LoginScreen(
            isLoading = authState.isLoading,
            errorMessage = authState.error,
            infoMessage = authState.infoMessage,
            onSignIn = authViewModel::signIn,
            onSignUp = authViewModel::signUp,
            resetStatus = resetStatus,
            onSendPasswordReset = authViewModel::sendPasswordReset,
            onDismissReset = authViewModel::clearResetStatus
        )
    } else {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Home.route,
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text("Home") },
                        icon = { Text("🏠") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Wardrobe.route,
                        onClick = {
                            navController.navigate(Screen.Wardrobe.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text("Wardrobe") },
                        icon = { Text("👕") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Scan.route,
                        onClick = {
                            navController.navigate(Screen.Scan.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text("Scan") },
                        icon = { Text("📷") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Map.route,
                        onClick = {
                            navController.navigate(Screen.Map.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text("Map") },
                        icon = { Text("📍") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Profile.route,
                        onClick = {
                            navController.navigate(Screen.Profile.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text("Profile") },
                        icon = { Text("👤") }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            viewModel = wardrobeViewModel,
                            onNavigateToScan = { navController.navigate(Screen.Scan.route) }
                        )
                    }
                    composable(Screen.Wardrobe.route) {
                        WardrobeScreen(viewModel = wardrobeViewModel)
                    }
                    composable(Screen.Scan.route) {
                        ScanScreen(
                            wardrobeViewModel = wardrobeViewModel
                        )
                    }
                    composable(Screen.Map.route) {
                        MapScreen()
                    }
                    composable(Screen.Profile.route) {
                        ProfileScreen(
                            email = authViewModel.userEmail,
                            onLogout = { authViewModel.signOut() }
                        )
                    }
                    composable(Screen.Stores.route) {
                        StoresScreen()
                    }
                }
            }
        }
    }
}
