package eka.dr.intl.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import eka.dr.intl.data.local.entity.BusinessEntity

@Dao
interface BusinessDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWorkspace(entity: BusinessEntity)

    @Query("SELECT * FROM business_entity order by IS_ACTIVE DESC")
    suspend fun getAllWorkspaces(): List<BusinessEntity>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateWorkspace(entity: BusinessEntity)

    @Query("SELECT * FROM business_entity WHERE IS_ACTIVE = 1 LIMIT 1")
    suspend fun getActiveWorkspace(): BusinessEntity?

    @Query("SELECT * FROM business_entity WHERE BID = :bid AND ID = :oid")
    suspend fun getWorkspaceById(bid: String, oid: String): BusinessEntity

    @Query("DELETE FROM business_entity WHERE BID = :bid AND ID = :oid")
    suspend fun deleteWorkspace(bid: String, oid: String)
}