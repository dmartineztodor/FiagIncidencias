package com.danimt.fiagincidencias.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.danimt.fiagincidencias.data.local.Incident
import com.danimt.fiagincidencias.data.local.Priority
import com.danimt.fiagincidencias.data.local.Status
import com.danimt.fiagincidencias.ui.MainViewModel
import com.danimt.fiagincidencias.ui.components.PriorityFilter
import com.danimt.fiagincidencias.ui.components.StatusFilter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Lista de dispositivos fija (según PDF)
val DEVICE_TYPES = listOf("PC", "Portátil", "Proyector", "Impresora", "Red", "Otro")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val incidents by viewModel.incidents.collectAsState()
    var incidentToEdit by remember { mutableStateOf<Incident?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Incidencias FIAG") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterSection(viewModel)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(incidents, key = { it.id }) { incident ->
                    IncidentCard(
                        incident = incident,
                        onClick = { incidentToEdit = incident }
                    )
                }
            }
        }
    }

    if (incidentToEdit != null) {
        EditIncidentDialog(
            incident = incidentToEdit!!,
            onDismiss = { incidentToEdit = null },
            onConfirm = { updatedIncident ->
                viewModel.updateIncident(updatedIncident)
                incidentToEdit = null
            }
        )
    }
}

@Composable
fun FilterSection(viewModel: MainViewModel) {
    StatusFilter(modifier = Modifier.fillMaxWidth(), onSelected = viewModel::setStatusFilter)
    PriorityFilter(modifier = Modifier.fillMaxWidth(), onSelected = viewModel::setPriorityFilter)

    // ✅ NUEVO: Filtro de Dispositivo
    DeviceTypeFilter(onSelected = viewModel::setDeviceFilter)

    var locationText by remember { mutableStateOf("") }
    OutlinedTextField(
        value = locationText,
        onValueChange = {
            locationText = it
            viewModel.setLocationFilter(it.ifBlank { null })
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Buscar por Ubicación") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceTypeFilter(onSelected: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<String?>(null) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected ?: "Tipo de Dispositivo",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            // ✅ CORRECCIÓN: Usamos la nueva sintaxis para menuAnchor
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Todos") },
                onClick = {
                    selected = null
                    expanded = false
                    onSelected(null)
                }
            )
            DEVICE_TYPES.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        selected = type
                        expanded = false
                        onSelected(type)
                    }
                )
            }
        }
    }
}

@Composable
fun IncidentCard(incident: Incident, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(incident.timestamp))

    OutlinedCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(incident.location, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Badge(containerColor = if(incident.status == Status.OPEN) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer) {
                    Text(incident.status.name, modifier = Modifier.padding(4.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Prioridad: ${incident.priority}", style = MaterialTheme.typography.bodySmall)
            Text("Equipo: ${incident.deviceType}", style = MaterialTheme.typography.bodySmall)
            Text("Fecha: $dateString", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(incident.description)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditIncidentDialog(incident: Incident, onDismiss: () -> Unit, onConfirm: (Incident) -> Unit) {
    var description by remember { mutableStateOf(incident.description) }
    var status by remember { mutableStateOf(incident.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Incidencia") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") }
                )
                Text("Cambiar Estado:")
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    Status.entries.forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s.name) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(incident.copy(description = description, status = status))
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}