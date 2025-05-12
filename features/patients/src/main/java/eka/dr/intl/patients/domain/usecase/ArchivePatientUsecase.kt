package eka.dr.intl.patients.domain.usecase

import eka.dr.intl.common.Resource
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.data.remote.dto.response.ArchivePatientResponse
import eka.dr.intl.patients.domain.repository.PatientDirectoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ArchivePatientUseCase(private val repository: PatientDirectoryRepository) {
    operator fun invoke(
        patient: PatientEntity
    ): Flow<Resource<ArchivePatientResponse>> = flow {
        emit(Resource.Loading())
        val data = repository.archivePatient(patient)
        if (!data?.code.isNullOrEmpty()) {
            emit(Resource.Error(data?.message ?: "Something went wrong!"))
            return@flow
        }
        data?.let {
            emit(Resource.Success(it))
        } ?: emit(Resource.Error("Something went wrong!"))
    }.catch { ex ->
        // Handle exceptions outside the flow builder using catch operator
        emit(Resource.Error(ex.localizedMessage ?: "Something went wrong!"))
    }
}