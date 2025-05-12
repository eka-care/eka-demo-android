package eka.dr.intl.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "business_entity", primaryKeys = ["bid", "id"])
data class BusinessEntity(
    @ColumnInfo(name = "bid") val bId: String,
    @ColumnInfo(name = "id") val oid: String,
    @ColumnInfo(name = "session") val session: String,
    @ColumnInfo(name = "refresh") val refresh: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "specialisation") val specialisation: String? = null,
    @ColumnInfo(name = "is_active") val active: Boolean = false,
    @ColumnInfo(name = "is_archived") val archived: Boolean = true,
    @ColumnInfo(name = "updated_at") val updateAt: Long = System.currentTimeMillis()
)
