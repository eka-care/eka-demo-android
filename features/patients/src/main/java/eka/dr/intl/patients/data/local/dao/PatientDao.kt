package eka.dr.intl.patients.data.local.dao


import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import eka.dr.intl.patients.data.local.entity.PatientEntity

@Dao
interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(patient: PatientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg patient: PatientEntity)

    @Query("SELECT * FROM patient_entity WHERE  business_id = :businessId AND archived = 0  ORDER BY updated_at DESC")
    fun getAll(businessId: String): PagingSource<Int, PatientEntity>

    @Query("SELECT * FROM patient_entity WHERE business_id = :businessId  AND oid = :oid")
    fun getPatient(oid: String, businessId: String): PatientEntity

    @Upsert
    fun update(patient: PatientEntity)

    @Query("SELECT * FROM patient_entity WHERE business_id = :businessId AND archived = 0 AND dirty = 1 AND new_patient = 0")
    fun getPatientsByUpdatedAt(businessId: String): List<PatientEntity>

    @Query("SELECT * FROM patient_entity WHERE business_id = :businessId AND archived = 0 AND new_patient = 1")
    fun getNewlyAddedPatients(businessId: String): List<PatientEntity>

    @Query(
        "SELECT * FROM patient_entity p WHERE p.archived = 0 AND p.business_id = :businessId AND p.oid  IN ( " +
                "SELECT oid FROM patient_entity_fts fts WHERE fts.name MATCH :query " +
                "UNION " +
                "SELECT oid FROM patient_entity_fts fts WHERE fts.uhid MATCH :query " +
                "UNION " +
                "SELECT oid FROM patient_entity_fts fts WHERE fts.phone MATCH :query ) ORDER BY p.updated_at DESC"
    )
    fun search(businessId: String, query: String): PagingSource<Int, PatientEntity>

    // get patient not synced count
    @Query("SELECT COUNT(oid) FROM patient_entity WHERE business_id = :businessId AND archived = 0 AND dirty = 1")
    fun getPatientNotSyncedCount(businessId: String): Int

    //get all patients count
    @Query("SELECT COUNT(oid) FROM patient_entity WHERE business_id = :businessId AND archived = 0")
    fun getPatientCount(businessId: String): Int

    // get patient who have uhid with limit and offset
    @Query("SELECT * FROM patient_entity WHERE business_id = :businessId AND archived = 0 AND uhid IS NOT NULL ORDER BY updated_at DESC")
    fun getPatientsWithUhid(
        businessId: String
    ): PagingSource<Int, PatientEntity>

    // get patient who have on app with limit and offset
    @Query("SELECT * FROM patient_entity WHERE business_id = :businessId AND archived = 0 AND on_app = 1 ORDER BY updated_at DESC")
    fun getPatientsWithOnApp(businessId: String): PagingSource<Int, PatientEntity>

    // get uhid count
    @Query("SELECT COUNT(uhid) FROM patient_entity WHERE business_id = :businessId  AND archived = 0 AND uhid IS NOT NULL")
    fun getUhidCount(businessId: String): Int

    // on app count
    @Query("SELECT COUNT(oid) FROM patient_entity WHERE business_id = :businessId AND archived = 0 AND on_app = 1")
    fun getOnAppCount(businessId: String): Int

    // get patient by ids
    @Query("SELECT * FROM patient_entity WHERE business_id = :businessId AND archived = 0 AND oid IN (:oids) ORDER BY updated_at DESC")
    fun getPatientsByIds(businessId: String, oids: List<String>): PagingSource<Int, PatientEntity>
}