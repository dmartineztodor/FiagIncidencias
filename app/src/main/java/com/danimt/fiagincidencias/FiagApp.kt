package com.danimt.fiagincidencias

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.danimt.fiagincidencias.worker.SUMMARY_WORK_NAME
import com.danimt.fiagincidencias.worker.SummaryWorker
import java.util.concurrent.TimeUnit

class FiagApp : Application() {
    override fun onCreate() {
        super.onCreate()
        scheduleSummaryWorker()
    }

    private fun scheduleSummaryWorker() {
        val request = PeriodicWorkRequestBuilder<SummaryWorker>(1, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SUMMARY_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
