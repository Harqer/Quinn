package com.musically.studio.engage

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

object EngagePublisher {

    fun publish(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val publishWorkRequest = OneTimeWorkRequestBuilder<EngageWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            Constants.WORKER_NAME,
            ExistingWorkPolicy.REPLACE,
            publishWorkRequest
        )
    }
}
