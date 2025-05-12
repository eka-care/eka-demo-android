package eka.dr.intl.patients.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import eka.dr.intl.patients.data.local.entity.DoctorEntity

@Dao
interface DoctorDao {
    // get all doctors from the database
    @Query("SELECT * FROM doctor_entity where business_id = :businessId")
    fun getAllDoctors(businessId: String): List<DoctorEntity>

    // insert doctors in the database bulk
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllDoctors(vararg tag: DoctorEntity)
}