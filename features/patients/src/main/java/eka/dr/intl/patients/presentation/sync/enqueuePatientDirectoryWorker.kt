package eka.dr.intl.patients.presentation.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun enqueuePatientDirectoryWorker(context: Context, bid: String) {
    val inputData = Data.Builder()
        .putString("bid", bid)
        .build()

    val workRequest = OneTimeWorkRequestBuilder<PatientDirectoryWorker>()
        .setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL,
            30,
            TimeUnit.SECONDS
        )
        .setInputData(inputData)
        .build()

    val workManager = WorkManager.getInstance(context)
    val workInfoList = workManager.getWorkInfosForUniqueWork("patient_dir_worker").get()

    val isWorkAlreadyEnqueued = workInfoList.any {
        it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING
    }

    if (!isWorkAlreadyEnqueued) {
        workManager.enqueueUniqueWork(
            "patient_dir_worker",
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }
}