package com.danimt.fiagincidencias.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {
    @Query("SELECT * FROM incidents")
    fun getAll(): Flow<List<Incident>>

    @Query("SELECT * FROM incidents WHERE id IN (:incidentIds)")
    fun loadAllByIds(incidentIds: IntArray): Flow<List<Incident>>

    @Query("SELECT * FROM incidents WHERE location LIKE :location LIMIT 1")
    fun findByName(location: String): Incident

    @Insert
    suspend fun insert(incident: Incident)

    @Delete
    suspend fun delete(incident: Incident)

    // ✅ NUEVO: Para actualizar incidencias editadas
    @Update
    suspend fun update(incident: Incident)

    // ✅ NUEVO: Para el Worker (evita errores de cancelación)
    @Query("SELECT * FROM incidents WHERE status = :status")
    suspend fun getByStatusSync(status: Status): List<Incident>
}