package eka.dr.intl.domain.usecase

import eka.dr.intl.data.local.entity.BusinessEntity
import eka.dr.intl.domain.repository.BusinessStoreRepository
import kotlinx.coroutines.flow.flow

class SignOutWorkspaceUseCase(private val businessStoreRepository: BusinessStoreRepository) {
    fun invoke(
        businessEntity: BusinessEntity,
        onLogout: () -> Unit,
        onSwitch: (switchTo: BusinessEntity) -> Unit
    ) = flow {
        val remainingWorkspaces = businessStoreRepository.getAllWorkspaces()
        val nextActiveWorkspace = remainingWorkspaces
            .filter { it.bId != businessEntity.bId && it.oid != businessEntity.oid }
            .filter { !it.archived }
            .maxByOrNull { it.updateAt }

        if (nextActiveWorkspace != null) {
            businessStoreRepository.deleteWorkspace(businessEntity.bId, businessEntity.oid)
            val nextActiveWorkspaceToSelect = nextActiveWorkspace.copy(
                active = true,
                updateAt = System.currentTimeMillis(),
                archived = false
            )
            businessStoreRepository.updateWorkspace(
                nextActiveWorkspaceToSelect
            )
            onSwitch(nextActiveWorkspaceToSelect)
        } else {
            onLogout.invoke()
        }
        emit(true)
    }
}