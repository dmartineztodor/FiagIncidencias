package com.danimt.fiagincidencias.data.repository

import com.danimt.fiagincidencias.data.local.Incident
import com.danimt.fiagincidencias.data.local.IncidentDao
import com.danimt.fiagincidencias.data.local.Status
import kotlinx.coroutines.flow.Flow

class IncidentRepository(private val dao: IncidentDao) {
    fun getAll(): Flow<List<Incident>> = dao.getAll()
    fun getByStatus(status: Status): Flow<List<Incident>> = dao.getByStatus(status)
    suspend fun add(incident: Incident) = dao.insert(incident)
}
