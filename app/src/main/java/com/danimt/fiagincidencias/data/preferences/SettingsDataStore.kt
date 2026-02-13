package com.danimt.fiagincidencias.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        val THEME_MODE = booleanPreferencesKey("theme_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        // ✅ NUEVO: Clave para el email
        val MANAGER_EMAIL = stringPreferencesKey("manager_email")
    }

    val themeMode: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[THEME_MODE] ?: false }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[NOTIFICATIONS_ENABLED] ?: true }

    // ✅ NUEVO: Flow para leer el email
    val managerEmail: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[MANAGER_EMAIL] ?: "admin@fiag.es" } // Valor por defecto

    suspend fun saveThemeMode(isDark: Boolean) {
        context.dataStore.edit { preferences -> preferences[THEME_MODE] = isDark }
    }

    suspend fun saveNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[NOTIFICATIONS_ENABLED] = enabled }
    }

    // ✅ NUEVO: Guardar email
    suspend fun saveManagerEmail(email: String) {
        context.dataStore.edit { preferences -> preferences[MANAGER_EMAIL] = email }
    }
}