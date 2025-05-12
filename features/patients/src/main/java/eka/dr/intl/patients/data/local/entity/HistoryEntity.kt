package eka.dr.intl.patients.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity


@Entity(tableName = "history_entity", primaryKeys = ["id", "business_id"])
data class HistoryEntity(
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "business_id") val businessId: String,
    @ColumnInfo(name = "last_updated_at") val lastUpdatedAt: Long = 0L,
)
