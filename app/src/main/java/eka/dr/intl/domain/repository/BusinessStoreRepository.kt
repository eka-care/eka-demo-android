package eka.dr.intl.domain.repository

import eka.dr.intl.data.local.entity.BusinessEntity


interface BusinessStoreRepository {
    suspend fun addWorkspace(businessEntity: BusinessEntity)
    suspend fun getAllWorkspaces(): List<BusinessEntity>
    suspend fun updateWorkspace(businessEntity: BusinessEntity)
    suspend fun updateSpecialisationWorkspace(bid: String, oid: String, specialisation: String)
    suspend fun getActiveWorkspace(): BusinessEntity?
    suspend fun deleteWorkspace(bid: String, oid: String)
}