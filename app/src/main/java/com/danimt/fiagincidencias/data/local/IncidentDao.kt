package com.danimt.fiagincidencias.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(incident: Incident)

    @Query("SELECT * FROM incidents ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Incident>>

    @Query("SELECT * FROM incidents WHERE status = :status ORDER BY timestamp DESC")
    fun getByStatus(status: Status): Flow<List<Incident>>
}
