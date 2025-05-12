package eka.dr.intl.data.repository

import eka.dr.intl.data.local.db.WorkspaceDatabase
import eka.dr.intl.data.local.entity.BusinessEntity
import eka.dr.intl.domain.repository.BusinessStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BusinessStoreRepositoryImpl(private val db: WorkspaceDatabase) :
    BusinessStoreRepository {
    override suspend fun addWorkspace(businessEntity: BusinessEntity) {
        withContext(Dispatchers.IO) {
            db.businessDao().addWorkspace(businessEntity)
        }
    }

    override suspend fun getAllWorkspaces(): List<BusinessEntity> {
        return withContext(Dispatchers.IO) {
            db.businessDao().getAllWorkspaces()
        }
    }

    override suspend fun updateWorkspace(businessEntity: BusinessEntity) {
        withContext(Dispatchers.IO) {
            db.businessDao().updateWorkspace(businessEntity)
        }
    }

    override suspend fun deleteWorkspace(bid: String, oid: String) {
        withContext(Dispatchers.IO) {
            db.businessDao().deleteWorkspace(bid, oid)
        }
    }

    override suspend fun updateSpecialisationWorkspace(
        bid: String,
        oid: String,
        specialisation: String
    ) {
        withContext(Dispatchers.IO) {
            val businessEntity = db.businessDao().getWorkspaceById(bid, oid)
            db.businessDao().updateWorkspace(
                businessEntity.copy(
                    specialisation = specialisation,
                    updateAt = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun getActiveWorkspace(): BusinessEntity? {
        return withContext(Dispatchers.IO) {
            db.businessDao().getActiveWorkspace()
        }
    }
}