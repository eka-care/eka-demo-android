package eka.dr.intl.patients.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import eka.dr.intl.patients.data.local.entity.HistoryEntity

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(history: HistoryEntity)

    @Query("UPDATE history_entity SET last_updated_at = :lastUpdatedAt WHERE id = :id AND business_id = :businessId")
    fun updateHistory(id: String, lastUpdatedAt: Long, businessId: String)

    @Query("SELECT * FROM history_entity WHERE id = :id AND business_id = :businessId")
    fun getHistoryById(id: String, businessId: String): HistoryEntity
}