package eka.dr.intl.patients.domain.usecase

import eka.dr.intl.common.Resource
import eka.dr.intl.patients.data.remote.dto.response.GenerateUHIDResponse
import eka.dr.intl.patients.domain.repository.PatientDirectoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GenerateUhidUseCase(private val repository: PatientDirectoryRepository) {
    operator fun invoke(): Flow<Resource<GenerateUHIDResponse>> = flow {
        try {
            emit(Resource.Loading())
            val data = repository.generateUHID()
            if (data?.success != true) {
                emit(Resource.Error("Something went wrong!", data))
                return@flow
            }
            data?.let {
                emit(Resource.Success(it))
            } ?: emit(Resource.Error("Something went wrong!"))
        } catch (ex: Exception) {
            emit(Resource.Error(ex.localizedMessage ?: "Something went wrong!"))
        }
    }
}