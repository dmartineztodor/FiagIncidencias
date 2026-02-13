package com.danimt.fiagincidencias.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddIncident : Screen("add_incident") // ✅ ESTA ES LA QUE TE FALTABA
    data object Settings : Screen("settings")
}