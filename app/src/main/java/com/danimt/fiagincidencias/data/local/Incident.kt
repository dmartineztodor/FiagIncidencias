package com.danimt.fiagincidencias.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidents")
data class Incident(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val location: String,
    val priority: Priority,
    val deviceType: String,
    val deviceId: String,
    val description: String,
    val timestamp: Long,
    val status: Status
)

enum class Priority { LOW, MEDIUM, HIGH }

enum class Status { OPEN, IN_PROGRESS, RESOLVED }
