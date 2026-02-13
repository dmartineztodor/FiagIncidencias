package com.danimt.fiagincidencias.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danimt.fiagincidencias.data.local.Incident
import com.danimt.fiagincidencias.data.local.Priority
import com.danimt.fiagincidencias.data.local.Status
import com.danimt.fiagincidencias.data.preferences.SettingsDataStore
import com.danimt.fiagincidencias.data.repository.IncidentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: IncidentRepository,
    private val settingsDataStore: SettingsDataStore

) : ViewModel() {
    // Filtros
    private val _statusFilter = MutableStateFlow<Status?>(null)
    private val _priorityFilter = MutableStateFlow<Priority?>(null)
    private val _locationFilter = MutableStateFlow<String?>(null)
    private val _deviceFilter = MutableStateFlow<String?>(null) // ✅ NUEVO
    val managerEmail = settingsDataStore.managerEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "admin@fiag.es")
    // Lista filtrada combinando todo
    val incidents = combine(
        repository.allIncidents,
        _statusFilter,
        _priorityFilter,
        _locationFilter,
        _deviceFilter
    ) { list, status, priority, location, device ->
        list.filter { incident ->
            (status == null || incident.status == status) &&
                    (priority == null || incident.priority == priority) &&
                    (location == null || incident.location.contains(location, ignoreCase = true)) &&
                    (device == null || incident.deviceType == device) // ✅ NUEVO Lógica filtro
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Ajustes
    val isDarkTheme = settingsDataStore.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // ✅ NUEVO: Estado de notificaciones
    val notificationsEnabled = settingsDataStore.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Funciones de inserción y actualización
    fun insertIncident(incident: Incident) {
        viewModelScope.launch {
            repository.insert(incident)
        }
    }

    fun updateIncident(incident: Incident) { // ✅ NUEVO
        viewModelScope.launch {
            repository.update(incident)
        }
    }

    // Setters para filtros
    fun setStatusFilter(status: Status?) { _statusFilter.value = status }
    fun setPriorityFilter(priority: Priority?) { _priorityFilter.value = priority }
    fun setLocationFilter(location: String?) { _locationFilter.value = location }
    fun setDeviceFilter(device: String?) { _deviceFilter.value = device } // ✅ NUEVO

    // Setters para ajustes
    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch { settingsDataStore.saveThemeMode(isDark) }
    }

    fun setNotificationsEnabled(enabled: Boolean) { // ✅ NUEVO
        viewModelScope.launch { settingsDataStore.saveNotificationsEnabled(enabled) }
    }

    fun setManagerEmail(email: String) {
        viewModelScope.launch {
            settingsDataStore.saveManagerEmail(email)
        }
    }
}