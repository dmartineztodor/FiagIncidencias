package com.danimt.fiagincidencias.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.danimt.fiagincidencias.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()

    // Leemos el email guardado en la BD
    val storedEmail by viewModel.managerEmail.collectAsState()

    // Variable temporal para lo que escribes en la caja de texto
    var emailText by remember(storedEmail) { mutableStateOf(storedEmail) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- SECCIÓN 1: TEMA ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tema Oscuro")
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { viewModel.toggleTheme(it) }
                )
            }

            HorizontalDivider()

            // --- SECCIÓN 2: NOTIFICACIONES E IA ---
            Text("Informes Automáticos (IA)", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Activar envío por email")
                    Text(
                        "Genera un resumen cada hora.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                )
            }

            // --- SECCIÓN 3: EMAIL DEL RESPONSABLE ---
            OutlinedTextField(
                value = emailText,
                onValueChange = { emailText = it }, // Solo actualiza la variable local
                label = { Text("Email del Responsable") },
                placeholder = { Text("ejemplo@fiag.es") },
                modifier = Modifier.fillMaxWidth(),
                enabled = notificationsEnabled, // Se desactiva si el switch está apagado
                singleLine = true
            )

            // ✅ BOTÓN DE GUARDAR (Ahora sí)
            Button(
                onClick = {
                    viewModel.setManagerEmail(emailText)
                    Toast.makeText(context, "Email guardado correctamente", Toast.LENGTH_SHORT).show()
                },
                // El botón solo se activa si hay texto y es diferente al guardado
                enabled = notificationsEnabled && emailText.isNotBlank() && emailText.contains("@"),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Email")
            }
        }
    }
}