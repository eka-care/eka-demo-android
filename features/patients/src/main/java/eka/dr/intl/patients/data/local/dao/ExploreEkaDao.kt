package eka.dr.intl.patients.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import eka.dr.intl.patients.data.local.entity.ExploreEkaEntity

@Dao
interface ExploreEkaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEkaCards(favCardEntities: List<ExploreEkaEntity>)

    @Query("SELECT * FROM explore_eka_table")
    suspend fun getAllCards(): List<ExploreEkaEntity>

}