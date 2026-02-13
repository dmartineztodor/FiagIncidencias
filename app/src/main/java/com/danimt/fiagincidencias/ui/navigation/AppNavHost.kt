package com.danimt.fiagincidencias.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.danimt.fiagincidencias.ui.MainViewModel
import com.danimt.fiagincidencias.ui.screens.AddIncidentScreen
import com.danimt.fiagincidencias.ui.screens.HomeScreen
import com.danimt.fiagincidencias.ui.screens.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Pantalla Principal
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate(Screen.AddIncident.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }

        // Pantalla de Añadir (Corrección: onBackClick)
        composable(Screen.AddIncident.route) {
            AddIncidentScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() } // ✅ Ahora coincide con el parámetro
            )
        }

        // Pantalla de Ajustes (Corrección: onBackClick)
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() } // ✅ Ahora coincide con el parámetro
            )
        }
    }
}