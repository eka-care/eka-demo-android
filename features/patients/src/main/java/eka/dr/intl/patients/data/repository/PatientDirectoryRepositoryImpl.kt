package eka.dr.intl.patients.data.repository

import com.haroldadmin.cnradapter.NetworkResponse
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.utility.DateUtils
import eka.dr.intl.network.Networking
import eka.dr.intl.patients.BuildConfig
import eka.dr.intl.patients.data.local.convertors.Converters
import eka.dr.intl.patients.data.local.entity.HistoryEntity
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.data.remote.api.PatientDirectory
import eka.dr.intl.patients.data.remote.api.PatientDirectoryNew
import eka.dr.intl.patients.data.remote.dto.request.AddPatientRequest
import eka.dr.intl.patients.data.remote.dto.request.UpdatePatientRequest
import eka.dr.intl.patients.data.remote.dto.response.AddPatientResponse
import eka.dr.intl.patients.data.remote.dto.response.ArchivePatientResponse
import eka.dr.intl.patients.data.remote.dto.response.GenerateUHIDResponse
import eka.dr.intl.patients.data.remote.dto.response.GetFormFieldsResponse
import eka.dr.intl.patients.data.remote.dto.response.GetPatientByIdResponse
import eka.dr.intl.patients.data.remote.dto.response.PatientDirectoryResponse
import eka.dr.intl.patients.domain.repository.HistoryStoreRepository
import eka.dr.intl.patients.domain.repository.PatientDirectoryRepository
import eka.dr.intl.patients.domain.repository.PatientStoreRepository
import eka.dr.intl.patients.utils.Conversions
import eka.dr.intl.patients.utils.HistoryStoreKeys
import eka.dr.intl.patients.utils.PatientDirectoryUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PatientDirectoryRepositoryImpl(
    private val historyStoreRepository: HistoryStoreRepository,
    private val patientStoreRepository: PatientStoreRepository,
) : PatientDirectoryRepository {
    private val remoteDataSource: PatientDirectory =
        Networking.create(PatientDirectory::class.java, BuildConfig.PARCHI_URL)
    private val aortaGo =
        Networking.create(PatientDirectoryNew::class.java, BuildConfig.AUTH)

    override suspend fun getPatientById(patientOid: String): GetPatientByIdResponse? {
        return withContext(Dispatchers.IO) {
            val response =
                when (val response =
                    aortaGo.getPatientById(patientOid)) {
                    is NetworkResponse.Success -> response.body
                    is NetworkResponse.ServerError -> if (response.code in 400..499) {
                        response.body
                    } else {
                        null
                    }

                    is NetworkResponse.NetworkError -> null
                    is NetworkResponse.UnknownError -> null
                }
            if (response != null && !response?.oid.isNullOrEmpty()) {
                val patientEntity = getPatientByIdFormatterToPatientEntity(response)
                patientStoreRepository.updatePatientData(
                    patientEntity
                )
            }
            response
        }
    }

    override suspend fun getPatientDirectory(bid: String): PatientDirectoryResponse? {
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

        return withContext(Dispatchers.IO) {
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
            }
            finalResponse
        }
    }

    override suspend fun archivePatient(patient: PatientEntity): ArchivePatientResponse? {
        return withContext(Dispatchers.IO) {
            val response =
                when (val response =
                    aortaGo.archivePatient(patient.oid)) {
                    is NetworkResponse.Success -> {
                        patientStoreRepository.updatePatientData(patient.copy(archived = true))
                        response.body
                    }

                    is NetworkResponse.ServerError -> if (response.code in 400..499) {
                        response.body
                    } else {
                        null
                    }

                    is NetworkResponse.NetworkError -> null
                    is NetworkResponse.UnknownError -> null
                }
            response
        }

    }

    override suspend fun addPatientToDirectory(
        patient: AddPatientRequest,
    ): AddPatientResponse? {
        return withContext(Dispatchers.IO) {
            val response =
                when (val response =
                    aortaGo.addPatientToDirectory(
                        patient
                    )) {
                    is NetworkResponse.Success -> {
                        response.body
                    }

                    is NetworkResponse.ServerError -> if (response.code in 400..499) {
                        response.body
                    } else {
                        null
                    }

                    is NetworkResponse.NetworkError -> null
                    is NetworkResponse.UnknownError -> null
                }
            response
        }
    }

    override suspend fun updatePatientInDirectory(
        patientOid: String,
        patient: UpdatePatientRequest,
    ): AddPatientResponse? {
        return withContext(Dispatchers.IO) {
            val response =
                when (val response =
                    aortaGo.updatePatientInDirectory(
                        patientOid = patientOid,
                        data = patient
                    )) {
                    is NetworkResponse.Success -> {
                        response.body
                    }

                    is NetworkResponse.ServerError -> if (response.code in 400..499) {
                        response.body
                    } else {
                        null
                    }

                    is NetworkResponse.NetworkError -> null
                    is NetworkResponse.UnknownError -> null
                }
            response
        }
    }

    override suspend fun generateUHID(): GenerateUHIDResponse? {
        return withContext(Dispatchers.IO) {
            val response =
                when (val response =
                    remoteDataSource.generateUHID(hashMapOf())) {
                    is NetworkResponse.Success -> response.body
                    is NetworkResponse.ServerError -> if (response.code in 400..499) {
                        response.body
                    } else {
                        null
                    }

                    is NetworkResponse.NetworkError -> null
                    is NetworkResponse.UnknownError -> null
                }
            response
        }
    }

    override suspend fun getFormFields(): GetFormFieldsResponse? {
        return withContext(Dispatchers.IO) {
            val response =
                when (val response =
                    remoteDataSource.getFormFields()) {
                    is NetworkResponse.Success -> response.body
                    is NetworkResponse.ServerError -> if (response.code in 400..499) {
                        response.body
                    } else {
                        null
                    }

                    is NetworkResponse.NetworkError -> null
                    is NetworkResponse.UnknownError -> null
                }
            response
        }
    }

    override suspend fun syncAddAndUpdatePatientFromLocal(): Boolean? {
        return withContext(Dispatchers.IO) {
            try {
                val businessId = OrbiUserManager.getSelectedBusiness() ?: ""

                val lastSync = historyStoreRepository.getHistoryDataById(
                    HistoryStoreKeys.PATIENT_DIR_LAST_SYNCED.key,
                    businessId
                )

                val newPatients = patientStoreRepository.getNewlyAddedPatientList()
                newPatients.forEach { patientEntity ->
                    addOrUpdatePatientToDirectory(patientEntity, false)
                }
                if (lastSync != 0L) {
                    val updatedPatients = patientStoreRepository.getPatientByUpdatedAt()
                    updatedPatients.forEach { patientEntity ->
                        addOrUpdatePatientToDirectory(patientEntity, true)
                    }
                    historyStoreRepository.upsertHistoryData(
                        HistoryEntity(
                            id = HistoryStoreKeys.PATIENT_DIR_LAST_SYNCED.key,
                            businessId = businessId,
                            lastUpdatedAt = DateUtils.getCurrentEpochTime()
                        )
                    )
                }
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    override suspend fun addOrUpdatePatientToDirectory(
        patient: PatientEntity,
        update: Boolean
    ): String? {
        return withContext(Dispatchers.IO) {
            val response = if (update) {
                updatePatientInDirectory(
                    patientOid = patient.oid,
                    PatientDirectoryUtils.formatPatientEntityToUpdatePatientRequest(
                        patient,
                    )
                )
            } else {
                addPatientToDirectory(
                    PatientDirectoryUtils.formatPatientEntityToAddPatientRequest(
                        patient,
                    )
                )
            }

            if (response?.code == "PROFILE_EXISTS") {
                throw Exception(response.message ?: "Profile already exists")
            }

            if (response != null) {
                patientStoreRepository.updatePatientData(
                    patient.copy(
                        uuid = response.uuid ?: response.customUuid,
                        dirty = false,
                        newPatient = false
                    )
                )
            } else {
                patientStoreRepository.addPatientData(patient.copy(dirty = true))
            }
            null
        }
    }
}

fun getPatientByIdFormatterToPatientEntity(data: GetPatientByIdResponse): PatientEntity {
    val businessId =
        OrbiUserManager.getSelectedBusiness() ?: "b-${OrbiUserManager.getUserTokenData()?.oid}"

    // pick last 10 digits
    val mobile = if (!data.mobile.isNullOrEmpty()) data.mobile.substring(data.mobile.length - 10)
        .toLong() else null
    val countryCode = if (!data.mobile.isNullOrEmpty())
        data.mobile.substring(0, data.mobile.length - 10).replace("[^0-9]".toRegex(), "")
            .toInt() else null
    val gender = Converters().toGenderFromString(data.gen)
    val abhaId = data?.healthIds?.firstOrNull()
    return PatientEntity(
        oid = data.oid,
        businessId = businessId,
        uuid = data.uuid,
        name = data.fln,
        age = Conversions.formYYYYMMDDToLong(data.dob),
        phone = mobile,
        countryCode = countryCode,
        gender = gender,
        createdAt = DateUtils.getCurrentEpochTime(),
        archived = false,
        updatedAt = DateUtils.getCurrentEpochTime(),
        onApp = false,
        newPatient = false,
        dirty = false,
        formData = "[]"
    )
}