package com.danimt.fiagincidencias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.danimt.fiagincidencias.data.local.AppDatabase
import com.danimt.fiagincidencias.data.preferences.SettingsDataStore
import com.danimt.fiagincidencias.data.repository.IncidentRepository
import com.danimt.fiagincidencias.ui.MainViewModel
import com.danimt.fiagincidencias.ui.MainViewModelFactory
import com.danimt.fiagincidencias.ui.navigation.AppNavHost

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val db = AppDatabase.get(applicationContext)
        val repository = IncidentRepository(db.incidentDao())
        val settings = SettingsDataStore(applicationContext)
        MainViewModelFactory(repository, settings)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MaterialTheme {
                Surface {
                    AppNavHost(
                        navController = navController,
                        viewModel = viewModel,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}