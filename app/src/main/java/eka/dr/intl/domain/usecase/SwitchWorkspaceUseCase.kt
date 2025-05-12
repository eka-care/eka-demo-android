package eka.dr.intl.domain.usecase

import eka.dr.intl.data.local.entity.BusinessEntity
import eka.dr.intl.domain.repository.BusinessStoreRepository
import kotlinx.coroutines.flow.flow

class SwitchWorkspaceUseCase(private val businessStoreRepository: BusinessStoreRepository) {
    fun invoke(switchToWorkspace: BusinessEntity) = flow {
        val activeWorkspace = businessStoreRepository.getAllWorkspaces().filter { it.active }
        activeWorkspace.forEach {
            businessStoreRepository.updateWorkspace(it.copy(active = false))
        }
        businessStoreRepository.updateWorkspace(
            switchToWorkspace.copy(
                active = true,
                updateAt = System.currentTimeMillis(),
                archived = false
            )
        )
        emit(true)
    }
}