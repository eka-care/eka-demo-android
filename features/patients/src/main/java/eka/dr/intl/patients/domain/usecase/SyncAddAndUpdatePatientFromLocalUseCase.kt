package eka.dr.intl.patients.domain.usecase

import eka.dr.intl.common.Resource
import eka.dr.intl.patients.domain.repository.PatientDirectoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SyncAddAndUpdatePatientFromLocalUseCase(
    private val repository: PatientDirectoryRepository
) {
    operator fun invoke(): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading())
            val data = repository.syncAddAndUpdatePatientFromLocal()
            data?.let {
                emit(Resource.Success(it))
            } ?: emit(Resource.Error("Something went wrong!"))

        } catch (ex: Exception) {
            emit(Resource.Error(ex.localizedMessage ?: "Something went wrong!"))
        }
    }
}