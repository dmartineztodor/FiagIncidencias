package com.danimt.fiagincidencias.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.danimt.fiagincidencias.data.preferences.SettingsDataStore
import com.danimt.fiagincidencias.data.repository.IncidentRepository

class MainViewModelFactory(
    private val repository: IncidentRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, settingsDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
