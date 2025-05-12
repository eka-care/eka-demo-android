package eka.dr.intl.patients.domain.usecase


import androidx.paging.Pager
import androidx.paging.PagingData
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.domain.repository.PatientStoreRepository
import kotlinx.coroutines.flow.Flow

class GetSearchPatientUseCase(private val repository: PatientStoreRepository) {
    fun get(query: String): Flow<PagingData<PatientEntity>> {
        return Pager(
            config = pageConfig,
            pagingSourceFactory = {
                repository.searchPatientData(query)
            }
        ).flow
    }
}