package com.example.fitspot.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Wardrobe : Screen("wardrobe")
    object Scan : Screen("scan")
    object Map : Screen("map")
}
