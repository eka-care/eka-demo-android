package eka.dr.intl.domain.usecase

import eka.dr.intl.domain.repository.BusinessStoreRepository

class UpdateSpecialisationWorkspaceUseCase(private val businessStoreRepository: BusinessStoreRepository) {
    suspend fun invoke(bid: String, oid: String, specialisation: String) {
        businessStoreRepository.updateSpecialisationWorkspace(
            bid,
            oid,
            specialisation
        )
    }
}