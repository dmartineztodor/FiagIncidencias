package com.danimt.fiagincidencias.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.danimt.fiagincidencias.R
import com.danimt.fiagincidencias.data.local.AppDatabase
import com.danimt.fiagincidencias.data.local.Status
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException

const val SUMMARY_WORK_NAME = "summary_worker"

class SummaryWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    // ⚠️ Pega tu API Key aquí (https://aistudio.google.com/app/apikey)
    private val apiKey = "AIzaSyCu4lXvuAl_HL3GunZGmUFn1edk1p-nZhQ"

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val dao = AppDatabase.get(applicationContext).incidentDao()

            // 1. Usamos la versión síncrona para evitar JobCancellationException
            val openIncidents = dao.getByStatusSync(Status.OPEN)

            if (openIncidents.isEmpty()) {
                Log.i("SummaryWorker", "No hay incidencias abiertas. No se envía resumen.")
                return@withContext Result.success()
            }

            // 2. Crear el Prompt para la IA
            val prompt = buildString {
                append("Genera un resumen ejecutivo muy breve de estas incidencias IT para el director:\n")
                openIncidents.forEach {
                    append("- [${it.priority}] ${it.location}: ${it.description}\n")
                }
                append("\nAgrupa por urgencia. Sé conciso.")
            }

            // 3. Llamar a Gemini
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey
            )

            val response = generativeModel.generateContent(prompt)
            val resumenIA = response.text ?: "Error al generar resumen."

            Log.i("SummaryWorker", "Resumen generado: $resumenIA")

            // 4. Notificar (Simulación de envío de correo)
            sendNotification(resumenIA)

            Result.success()

        } catch (e: CancellationException) {
            // Importante: Dejar pasar la cancelación para que Android gestione la batería
            throw e
        } catch (e: Exception) {
            Log.e("SummaryWorker", "Error crítico en el worker", e)
            Result.retry()
        }
    }

    private fun sendNotification(message: String) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "fiag_report"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Reportes FIAG", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_email) // Asegúrate de tener un icono o usa este genérico
            .setContentTitle("Resumen de Incidencias (IA)")
            .setContentText("Despliega para ver el reporte...")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        manager.notify(999, notification)
    }
}