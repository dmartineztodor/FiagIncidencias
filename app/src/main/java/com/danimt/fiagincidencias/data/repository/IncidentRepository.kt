package com.danimt.fiagincidencias.data.repository

import com.danimt.fiagincidencias.data.local.Incident
import com.danimt.fiagincidencias.data.local.IncidentDao
import kotlinx.coroutines.flow.Flow

class IncidentRepository(private val incidentDao: IncidentDao) {

    val allIncidents: Flow<List<Incident>> = incidentDao.getAll()

    suspend fun insert(incident: Incident) {
        incidentDao.insert(incident)
    }

    // ✅ NUEVO: Función para actualizar
    suspend fun update(incident: Incident) {
        incidentDao.update(incident)
    }
}