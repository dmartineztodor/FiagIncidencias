package com.danimt.fiagincidencias.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val incidents by viewModel.incidents.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Incidentes") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Añadir incidente")
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
            FilterRow(
                onStatusSelected = viewModel::setStatusFilter,
                onPrioritySelected = viewModel::setPriorityFilter,
                onLocationChanged = viewModel::setLocationFilter
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(incidents, key = { it.id }) { incident ->
                    IncidentCard(incident = incident)
                }
            }
        }
    }
}

@Composable
private fun FilterRow(
    onStatusSelected: (Status?) -> Unit,
    onPrioritySelected: (Priority?) -> Unit,
    onLocationChanged: (String?) -> Unit
) {
    var locationText by remember { mutableStateOf("") }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusFilter(onSelected = onStatusSelected)
        PriorityFilter(onSelected = onPrioritySelected)
    }
    Spacer(modifier = Modifier.padding(4.dp))
    OutlinedTextField(
        value = locationText,
        onValueChange = {
            locationText = it
            onLocationChanged(it.ifBlank { null })
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Ubicación") },
        placeholder = { Text("Aula, oficina...") }
    )
}

@Composable
private fun IncidentCard(incident: Incident) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = incident.location,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(text = incident.status.name)
            }
            Text(text = "Prioridad: ${incident.priority.name}")
            Text(text = "Equipo: ${incident.deviceType} (${incident.deviceId})")
            Text(text = incident.description)
        }
    }
}
