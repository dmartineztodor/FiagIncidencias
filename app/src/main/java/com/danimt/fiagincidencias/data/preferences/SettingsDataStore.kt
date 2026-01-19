package com.danimt.fiagincidencias.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "fiag_prefs")

class SettingsDataStore(private val context: Context) {
    private val emailKey = stringPreferencesKey("admin_email")

    val emailFlow: Flow<String?> = context.dataStore.data.map { it[emailKey] }

    suspend fun saveEmail(email: String) {
        context.dataStore.edit { prefs ->
            prefs[emailKey] = email
        }
    }
}
