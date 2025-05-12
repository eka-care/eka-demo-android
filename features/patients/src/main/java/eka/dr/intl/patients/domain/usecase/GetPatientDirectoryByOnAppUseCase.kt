package eka.dr.intl.patients.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.domain.repository.PatientStoreRepository
import kotlinx.coroutines.flow.Flow

val pageConfig = PagingConfig(
    pageSize = 20,
    enablePlaceholders = true,
    prefetchDistance = 1,
)

class GetPatientDirectoryByOnAppUseCase(
    private val repository: PatientStoreRepository,
) {
    fun get(): Flow<PagingData<PatientEntity>> {
        return Pager(
            config = pageConfig,
            pagingSourceFactory = {
                repository.getPatientListWithOnApp()
            }
        ).flow
    }
}