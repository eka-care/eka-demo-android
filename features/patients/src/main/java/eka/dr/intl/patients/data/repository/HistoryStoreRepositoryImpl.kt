package eka.dr.intl.patients.data.repository

import eka.dr.intl.patients.data.local.db.DoctorDatabase
import eka.dr.intl.patients.data.local.entity.HistoryEntity
import eka.dr.intl.patients.domain.repository.HistoryStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryStoreRepositoryImpl(val db: DoctorDatabase) : HistoryStoreRepository {
    override suspend fun getHistoryDataById(id: String, businessId: String): Long {
        return withContext(Dispatchers.IO) {
            db.historyDao().getHistoryById(id, businessId)?.lastUpdatedAt ?: 0L
        }
    }

    override suspend fun upsertHistoryData(historyStoreEntity: HistoryEntity) {
        return withContext(Dispatchers.IO) {
            val historyData =
                try {
                    db.historyDao()
                        .getHistoryById(historyStoreEntity.id, historyStoreEntity.businessId)
                } catch (e: Exception) {
                    null
                }
            if (historyData?.id.isNullOrEmpty()) {
                db.historyDao().insertHistory(historyStoreEntity)
            } else {
                db.historyDao().updateHistory(
                    id = historyStoreEntity.id,
                    lastUpdatedAt = historyStoreEntity.lastUpdatedAt,
                    businessId = historyStoreEntity.businessId
                )
            }
        }
    }
}