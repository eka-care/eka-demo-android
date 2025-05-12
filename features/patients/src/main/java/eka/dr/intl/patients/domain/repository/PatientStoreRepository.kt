package eka.dr.intl.patients.domain.repository

import androidx.paging.PagingSource
import eka.dr.intl.patients.data.local.entity.PatientEntity

interface PatientStoreRepository {
    suspend fun insertAllPatientData(patientStoreEntity: List<PatientEntity>)
    suspend fun getPatientDataByProfile(oid: String): PatientEntity
    suspend fun updatePatientData(patientStoreEntity: PatientEntity)
    suspend fun addPatientData(patientStoreEntity: PatientEntity)
    suspend fun getNewlyAddedPatientList(): List<PatientEntity>
    suspend fun getPatientByUpdatedAt(): List<PatientEntity>
    suspend fun getUhidPatientCount(): Int
    suspend fun getOnAppPatientCount(): Int
    suspend fun getPatientCount(): Int
    suspend fun getPatientNotSyncedCount(): Int

    fun getPatientByIds(oids: List<String>): PagingSource<Int, PatientEntity>
    fun searchPatientData(query: String): PagingSource<Int, PatientEntity>
    fun getPatientDataList(): PagingSource<Int, PatientEntity>
    fun getPatientListWithUhid(): PagingSource<Int, PatientEntity>
    fun getPatientListWithOnApp(): PagingSource<Int, PatientEntity>
}