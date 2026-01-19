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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: IncidentRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val statusFilter = MutableStateFlow<Status?>(null)
    private val priorityFilter = MutableStateFlow<Priority?>(null)
    private val locationFilter = MutableStateFlow<String?>(null)

    val incidents: StateFlow<List<Incident>> =
        combine(
            repository.getAll(),
            statusFilter,
            priorityFilter,
            locationFilter
        ) { list, status, priority, location ->
            list.filter { incident ->
                (status == null || incident.status == status) &&
                    (priority == null || incident.priority == priority) &&
                    (location.isNullOrBlank() || incident.location.equals(location, ignoreCase = true))
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val adminEmail: StateFlow<String?> =
        settingsDataStore.emailFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            null
        )

    fun setStatusFilter(status: Status?) {
        statusFilter.value = status
    }

    fun setPriorityFilter(priority: Priority?) {
        priorityFilter.value = priority
    }

    fun setLocationFilter(location: String?) {
        locationFilter.value = location
    }

    fun addIncident(incident: Incident) {
        viewModelScope.launch {
            repository.add(incident)
        }
    }

    fun saveEmail(email: String) {
        viewModelScope.launch {
            settingsDataStore.saveEmail(email)
        }
    }
}
