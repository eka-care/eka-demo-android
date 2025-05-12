package eka.dr.intl.domain.usecase

import eka.dr.intl.domain.repository.BusinessStoreRepository
import kotlinx.coroutines.flow.flow

class GetAllWorkspaceUseCase(private val businessStoreRepository: BusinessStoreRepository) {
    fun invoke() = flow {
        emit(businessStoreRepository.getAllWorkspaces())
    }
}