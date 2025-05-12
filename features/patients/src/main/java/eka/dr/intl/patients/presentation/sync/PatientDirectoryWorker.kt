package eka.dr.intl.patients.presentation.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.haroldadmin.cnradapter.NetworkResponse
import eka.dr.intl.patients.BuildConfig
import eka.dr.intl.common.utility.DateUtils
import eka.dr.intl.network.Networking
import eka.dr.intl.patients.data.local.entity.HistoryEntity
import eka.dr.intl.patients.data.remote.api.PatientDirectoryNew
import eka.dr.intl.patients.data.remote.dto.response.PatientDirectoryResponse
import eka.dr.intl.patients.di.PatientDirectoryKoin.Companion.HISTORY_STORE_REPOSITORY_IMPL
import eka.dr.intl.patients.di.PatientDirectoryKoin.Companion.PATIENT_STORE_REPOSITORY_IMPL
import eka.dr.intl.patients.domain.repository.HistoryStoreRepository
import eka.dr.intl.patients.domain.repository.PatientStoreRepository
import eka.dr.intl.patients.utils.HistoryStoreKeys
import eka.dr.intl.patients.utils.PatientDirectoryUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class PatientDirectoryWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val historyStoreRepository: HistoryStoreRepository by inject(
        named(
            HISTORY_STORE_REPOSITORY_IMPL
        )
    )
    private val patientStoreRepository: PatientStoreRepository by inject(
        named(
            PATIENT_STORE_REPOSITORY_IMPL
        )
    )
    private val aortaGo = Networking.create(PatientDirectoryNew::class.java, BuildConfig.AUTH)

    override suspend fun doWork(): Result {
        return try {
            val bid = inputData.getString("bid") ?: return Result.failure()
            val queryParams = hashMapOf<String, String>()
            queryParams["visits"] = "false"
            queryParams["pageSize"] = "2000"

            // get ISO string from local storage
            val lastUpdatedPatientDirectory = try {
                historyStoreRepository.getHistoryDataById(
                    HistoryStoreKeys.PATIENT_DIR_LAST_UPDATED.key,
                    bid
                )
            } catch (e: Exception) {
                0L
            }

            val from = PatientDirectoryUtils.getLastUpdatedPatientDirectoryISOString(
                lastUpdatedPatientDirectory
            )
            if (from.isNotEmpty()) {
                queryParams["from"] = from
            }

            val header = hashMapOf<String, String>()
            header["Accept-Encoding"] = "br;q=1.0, gzip;q=0.8, *;q=0.1"
            header["Content-Type"] = "application/json"

            var pageNo = 1
            var nextPage: Int
            var finalResponse: PatientDirectoryResponse? = null

            do {
                queryParams["pageNo"] = pageNo.toString()
                val response =
                    when (val response =
                        aortaGo.getPatientDirectory(map = queryParams, header)) {
                        is NetworkResponse.Success -> {
                            val body = response.body
                            if (body.data.isNotEmpty()) {
                                val patientData = body.data.mapNotNull { patientEntity ->
                                    PatientDirectoryUtils.formatter(patientEntity)
                                }
                                patientStoreRepository.insertAllPatientData(patientData)
                                historyStoreRepository.upsertHistoryData(
                                    HistoryEntity(
                                        id = HistoryStoreKeys.PATIENT_DIR_LAST_UPDATED.key,
                                        businessId = bid,
                                        lastUpdatedAt = DateUtils.getCurrentEpochTime()
                                    )
                                )
                            }
                            finalResponse = body
                            body.currPageMeta?.nextPage
                        }

                        is NetworkResponse.ServerError -> if (response.code in 400..499) {
                            response.body?.currPageMeta?.nextPage ?: -1
                        } else {
                            -1
                        }

                        is NetworkResponse.NetworkError -> -1
                        is NetworkResponse.UnknownError -> -1
                        else -> -1
                    }
                nextPage = response ?: -1
                pageNo++
            } while (nextPage != -1)

            if (finalResponse != null) {
                historyStoreRepository.upsertHistoryData(
                    HistoryEntity(
                        id = HistoryStoreKeys.PATIENT_DIR_LAST_SYNCED.key,
                        businessId = bid,
                        lastUpdatedAt = DateUtils.getCurrentEpochTime()
                    )
                )
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}