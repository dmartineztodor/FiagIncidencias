package com.danimt.fiagincidencias.data.local

import androidx.room.TypeConverter

class IncidentConverters {
    @TypeConverter
    fun priorityToString(priority: Priority?): String? = priority?.name

    @TypeConverter
    fun stringToPriority(value: String?): Priority? =
        value?.let { Priority.valueOf(it) }

    @TypeConverter
    fun statusToString(status: Status?): String? = status?.name

    @TypeConverter
    fun stringToStatus(value: String?): Status? =
        value?.let { Status.valueOf(it) }
}
