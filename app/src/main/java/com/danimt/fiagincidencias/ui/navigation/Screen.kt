package com.danimt.fiagincidencias.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Add : Screen("add")
    data object Settings : Screen("settings")
}
