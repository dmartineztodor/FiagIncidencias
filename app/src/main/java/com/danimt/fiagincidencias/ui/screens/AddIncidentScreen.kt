package com.danimt.fiagincidencias.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.danimt.fiagincidencias.data.local.Incident
import com.danimt.fiagincidencias.data.local.Priority
import com.danimt.fiagincidencias.data.local.Status
import com.danimt.fiagincidencias.ui.MainViewModel
import com.danimt.fiagincidencias.ui.components.PriorityFilter
import com.danimt.fiagincidencias.ui.components.StatusFilter

// Lista de tipos de dispositivos (igual que en HomeScreen)
private val DEVICE_TYPES_OPTIONS = listOf("PC", "Portátil", "Proyector", "Impresora", "Red", "Otro")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncidentScreen(
    viewModel: MainViewModel, // ✅ AHORA SÍ acepta el ViewModel
    onBackClick: () -> Unit   // ✅ AHORA SÍ se llama onBackClick
) {
    var location by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.LOW) }
    var deviceType by remember { mutableStateOf(DEVICE_TYPES_OPTIONS.first()) }
    var deviceId by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(Status.OPEN) }

    // Estado para el dropdown de tipo de dispositivo
    var deviceTypeExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Incidencia") },
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
                .verticalScroll(rememberScrollState()) // Permite scroll si el teclado tapa campos
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ubicación
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Ubicación") },
                placeholder = { Text("Ej: Aula 201") }
            )

            // Tipo de Equipo (Dropdown)
            ExposedDropdownMenuBox(
                expanded = deviceTypeExpanded,
                onExpandedChange = { deviceTypeExpanded = !deviceTypeExpanded }
            ) {
                OutlinedTextField(
                    value = deviceType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de equipo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deviceTypeExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = deviceTypeExpanded,
                    onDismissRequest = { deviceTypeExpanded = false }
                ) {
                    DEVICE_TYPES_OPTIONS.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                deviceType = type
                                deviceTypeExpanded = false
                            }
                        )
                    }
                }
            }

            // ID de Equipo
            OutlinedTextField(
                value = deviceId,
                onValueChange = { deviceId = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("ID de equipo") }
            )

            // Descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Descripción del problema") },
                minLines = 3
            )

            // ⚠️ AVISO DE PRIVACIDAD (Obligatorio para el 10)
            Text(
                text = "⚠️ No introducir datos personales sensibles.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )

            // Selectores
            Text("Estado Inicial:", style = MaterialTheme.typography.labelLarge)
            StatusFilter(
                modifier = Modifier.fillMaxWidth(),
                onSelected = { it?.let { status = it } }
            )

            Text("Prioridad:", style = MaterialTheme.typography.labelLarge)
            PriorityFilter(
                modifier = Modifier.fillMaxWidth(),
                onSelected = { it?.let { priority = it } }
            )

            Button(
                onClick = {
                    // Guardamos usando el ViewModel
                    viewModel.insertIncident(
                        Incident(
                            location = location,
                            priority = priority,
                            deviceType = deviceType,
                            deviceId = deviceId,
                            description = description,
                            timestamp = System.currentTimeMillis(),
                            status = status
                        )
                    )
                    onBackClick() // Volvemos atrás
                },
                enabled = location.isNotBlank() && description.isNotBlank(),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Guardar Incidencia")
            }
        }
    }
}