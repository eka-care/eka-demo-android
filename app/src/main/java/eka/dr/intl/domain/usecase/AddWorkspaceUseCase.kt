package eka.dr.intl.domain.usecase

import eka.dr.intl.data.local.entity.BusinessEntity
import eka.dr.intl.domain.repository.BusinessStoreRepository

class AddWorkspaceUseCase(private val businessStoreRepository: BusinessStoreRepository) {
    suspend fun invoke(businessEntity: BusinessEntity) {
        businessStoreRepository.getAllWorkspaces().forEach {
            businessStoreRepository.updateWorkspace(it.copy(active = false))
        }
        businessStoreRepository.addWorkspace(
            businessEntity.copy(
                active = true,
                updateAt = System.currentTimeMillis(),
                archived = false
            )
        )
    }
}