package eka.dr.intl.patients.domain.repository

import eka.dr.intl.patients.data.local.entity.HistoryEntity

interface HistoryStoreRepository {
    suspend fun getHistoryDataById(id: String, businessId: String): Long
    suspend fun upsertHistoryData(historyStoreEntity: HistoryEntity)
}