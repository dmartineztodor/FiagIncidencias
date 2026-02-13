package com.danimt.fiagincidencias.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.danimt.fiagincidencias.data.local.AppDatabase
import com.danimt.fiagincidencias.data.local.Status
import com.danimt.fiagincidencias.data.preferences.SettingsDataStore
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

const val SUMMARY_WORK_NAME = "summary_worker"

class SummaryWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    // ⚠️ IMPORTANTE: TU API KEY VA AQUÍ
    private val apiKey = "AIzaSyCu4lXvuAl_HL3GunZGmUFn1edk1p-nZhQ"

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // 1. LEER PREFERENCIAS
            val settings = SettingsDataStore(applicationContext)

            // A) Ver si está activado
            val isEnabled = settings.notificationsEnabled.first()
            if (!isEnabled) {
                Log.i("SummaryWorker", "Cancelado: Envío desactivado.")
                return@withContext Result.success()
            }

            // B) Ver si hay email configurado
            val emailDestino = settings.managerEmail.first()
            if (emailDestino.isBlank()) {
                Log.w("SummaryWorker", "Cancelado: No hay email configurado.")
                return@withContext Result.success()
            }

            // 2. BUSCAR INCIDENCIAS ABIERTAS
            val dao = AppDatabase.get(applicationContext).incidentDao()
            // Usamos .first() para obtener la lista actual del Flow
            val openIncidents = dao.getAll().first().filter { it.status == Status.OPEN }

            if (openIncidents.isEmpty()) {
                Log.i("SummaryWorker", "No hay incidencias abiertas.")
                return@withContext Result.success()
            }

            // 3. GENERAR RESUMEN CON IA (Gemini)
            val prompt = buildString {
                append("Genera un resumen ejecutivo para el responsable ($emailDestino).\n")
                append("Incidencias pendientes:\n")
                openIncidents.forEach {
                    append("- [Prioridad: ${it.priority}] ${it.location} (${it.deviceType}): ${it.description}\n")
                }
                append("\nAgrupa por prioridad. Sé breve y profesional.")
            }

            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey
            )

            val response = generativeModel.generateContent(prompt)
            val resumenIA = response.text ?: "Error al generar resumen."

            Log.i("SummaryWorker", "Resumen generado para $emailDestino")

            // 4. ENVIAR NOTIFICACIÓN (Simulando Email)
            sendNotification(emailDestino, resumenIA)

            Result.success()

        } catch (e: Exception) {
            Log.e("SummaryWorker", "Error en el worker", e)
            Result.retry()
        }
    }

    private fun sendNotification(email: String, contenido: String) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "fiag_email_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Envíos Automáticos IA",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("Informe enviado a: $email")
            .setContentText("Despliega para ver el resumen...")
            .setStyle(NotificationCompat.BigTextStyle().bigText(contenido))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(999, notification)
    }
}