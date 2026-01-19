package com.danimt.fiagincidencias.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.danimt.fiagincidencias.data.local.AppDatabase
import com.danimt.fiagincidencias.data.local.Status
import kotlinx.coroutines.flow.first

const val SUMMARY_WORK_NAME = "summary_worker"

class SummaryWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = try {
        val dao = AppDatabase.get(applicationContext).incidentDao()
        val openIncidents = dao.getByStatus(Status.OPEN).first()
        Log.i("SummaryWorker", "Incidentes abiertos: ${openIncidents.size}")
        openIncidents.forEach { incident ->
            Log.i(
                "SummaryWorker",
                "• ${incident.id} ${incident.location} ${incident.priority} ${incident.description}"
            )
        }
        Result.success()
    } catch (e: Exception) {
        Log.e("SummaryWorker", "Error generando resumen", e)
        Result.retry()
    }
}
