package com.danimt.fiagincidencias.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.danimt.fiagincidencias.data.local.Incident
import com.danimt.fiagincidencias.data.local.Priority
import com.danimt.fiagincidencias.data.local.Status
import com.danimt.fiagincidencias.ui.components.PriorityFilter
import com.danimt.fiagincidencias.ui.components.StatusFilter

@Composable
fun AddIncidentScreen(
    onSave: (Incident) -> Unit,
    onBack: () -> Unit
) {
    var location by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.LOW) }
    var deviceType by remember { mutableStateOf("") }
    var deviceId by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(Status.OPEN) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo incidente") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Ubicación") }
            )
            OutlinedTextField(
                value = deviceType,
                onValueChange = { deviceType = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Tipo de equipo") }
            )
            OutlinedTextField(
                value = deviceId,
                onValueChange = { deviceId = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("ID de equipo") }
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Descripción") },
                minLines = 2
            )
            StatusFilter(onSelected = { it?.let { status = it } })
            PriorityFilter(onSelected = { it?.let { priority = it } })
            Spacer(modifier = Modifier.padding(4.dp))
            Button(
                onClick = {
                    onSave(
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
                },
                enabled = location.isNotBlank() && description.isNotBlank()
            ) {
                Text("Guardar")
            }
        }
    }
}
