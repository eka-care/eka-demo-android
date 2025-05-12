package eka.dr.intl.patients.domain.usecase

import eka.dr.intl.common.Resource
import eka.dr.intl.patients.domain.repository.PatientStoreRepository
import eka.dr.intl.patients.presentation.viewModels.PatientCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetPatientCountUseCase(
    private val repository: PatientStoreRepository,
) {
    @Throws(Exception::class)
    operator fun invoke(): Flow<Resource<PatientCount>> = flow {
        emit(Resource.Loading())
        try {
            val allPatientCount = repository.getPatientCount()
            val uhidPatientCount = repository.getUhidPatientCount()
            val onAppPatientCount = repository.getOnAppPatientCount()
            val patientNotSyncedCount = repository.getPatientNotSyncedCount()
            emit(
                Resource.Success(
                    PatientCount(
                        allPatientCount = allPatientCount,
                        uhidPatientCount = uhidPatientCount,
                        onAppPatientCount = onAppPatientCount,
                        patientNotSyncedCount = patientNotSyncedCount
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Something went wrong!"))
        }
    }
}