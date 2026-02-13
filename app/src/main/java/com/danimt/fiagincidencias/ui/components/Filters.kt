package com.danimt.fiagincidencias.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.danimt.fiagincidencias.data.local.Priority
import com.danimt.fiagincidencias.data.local.Status

// ✅ CAMBIO: Añadimos .fillMaxWidth() a los TextField para que ocupen todo el ancho

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StatusFilter(
    modifier: Modifier = Modifier,
    onSelected: (Status?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<Status?>(null) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier // El modificador externo se aplica a la caja
    ) {
        OutlinedTextField(
            value = selected?.name ?: "Estado",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            // 👇 CAMBIO IMPORTANTE: .fillMaxWidth() añadido
            modifier = Modifier.menuAnchor().fillMaxWidth()
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
            Status.entries.forEach { status ->
                DropdownMenuItem(
                    text = { Text(status.name) },
                    onClick = {
                        selected = status
                        expanded = false
                        onSelected(status)
                    }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PriorityFilter(
    modifier: Modifier = Modifier,
    onSelected: (Priority?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<Priority?>(null) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected?.name ?: "Prioridad",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            // 👇 CAMBIO IMPORTANTE: .fillMaxWidth() añadido
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Todas") },
                onClick = {
                    selected = null
                    expanded = false
                    onSelected(null)
                }
            )
            Priority.entries.forEach { priority ->
                DropdownMenuItem(
                    text = { Text(priority.name) },
                    onClick = {
                        selected = priority
                        expanded = false
                        onSelected(priority)
                    }
                )
            }
        }
    }
}