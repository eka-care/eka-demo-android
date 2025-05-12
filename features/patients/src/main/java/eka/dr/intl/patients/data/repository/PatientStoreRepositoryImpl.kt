package eka.dr.intl.patients.data.repository

import androidx.paging.PagingSource
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.patients.data.local.db.DoctorDatabase
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.domain.repository.PatientStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class PatientStoreRepositoryImpl(private val patientDatabase: DoctorDatabase) :
    PatientStoreRepository {
    override suspend fun insertAllPatientData(patientStoreEntity: List<PatientEntity>) {
        withContext(Dispatchers.IO) {
            patientDatabase.dao().insertAll(*patientStoreEntity.toTypedArray())
        }
    }

    override suspend fun getPatientDataByProfile(oid: String): PatientEntity {
        val businessId = OrbiUserManager.getSelectedBusiness() ?: ""
        return withContext(Dispatchers.IO) {
            patientDatabase.dao().getPatient(oid, businessId)
        }
    }

    override fun getPatientDataList(): PagingSource<Int, PatientEntity> {
        val businessId = OrbiUserManager.getSelectedBusiness() ?: ""
        return patientDatabase.dao()
            .getAll(businessId = businessId)
    }

    override suspend fun updatePatientData(patientStoreEntity: PatientEntity) {
        withContext(Dispatchers.IO) {
            patientDatabase.dao().update(patientStoreEntity)
        }
    }

    override fun searchPatientData(
        query: String,
    ): PagingSource<Int, PatientEntity> {
        val businessId = OrbiUserManager.getSelectedBusiness() ?: ""
        val searchQuery = query
            .trim()
            .split(Regex("[^\\w]+"))
            .joinToString(" OR ") { "$it*" }
        return patientDatabase.dao()
            .search(
                businessId,
                query = searchQuery
            )
    }


    override suspend fun addPatientData(patientStoreEntity: PatientEntity) {
        withContext(Dispatchers.IO) {
            patientDatabase.dao().insert(patientStoreEntity)
        }
    }

    override suspend fun getNewlyAddedPatientList(): List<PatientEntity> {
        return withContext(Dispatchers.IO) {
            val businessId = OrbiUserManager.getSelectedBusiness() ?: ""
            patientDatabase.dao()
                .getNewlyAddedPatients(businessId)
        }
    }

    override suspend fun getPatientByUpdatedAt(): List<PatientEntity> {
        val businessId = OrbiUserManager.getSelectedBusiness() ?: ""
        return withContext(Dispatchers.IO) {
            patientDatabase.dao().getPatientsByUpdatedAt(businessId)
        }
    }

    override suspend fun getUhidPatientCount(): Int {
        return withContext(Dispatchers.IO) {
            val businessId = OrbiUserManager.getSelectedBusiness() ?: ""
            patientDatabase.dao().getUhidCount(businessId)
        }
    }

    override suspend fun getOnAppPatientCount(): Int {
        return withContext(Dispatchers.IO) {
            val businessId = OrbiUserManager.getSelectedBusiness() ?: ""
            patientDatabase.dao().getOnAppCount(businessId)
        }
    }

    override suspend fun getPatientCount(): Int {
        return withContext(Dispatchers.IO) {
            val businessId = OrbiUserManager.getSelectedBusiness() ?: ""
            patientDatabase.dao().getPatientCount(businessId)
        }
    }

    override suspend fun getPatientNotSyncedCount(): Int {
        return withContext(Dispatchers.IO) {
            val businessId = OrbiUserManager.getSelectedBusiness() ?: ""
            patientDatabase.dao().getPatientNotSyncedCount(businessId)
        }
    }

    override fun getPatientByIds(ids: List<String>): PagingSource<Int, PatientEntity> {
        val businessId = OrbiUserManager.getSelectedBusiness() ?: ""
        return patientDatabase.dao().getPatientsByIds(businessId, ids)
    }

    override fun getPatientListWithUhid(): PagingSource<Int, PatientEntity> {
        val businessId = OrbiUserManager.getSelectedBusiness() ?: ""
        return patientDatabase.dao().getPatientsWithUhid(businessId)
    }


    override fun getPatientListWithOnApp(): PagingSource<Int, PatientEntity> {
        val businessId = OrbiUserManager.getSelectedBusiness() ?: ""
        return patientDatabase.dao().getPatientsWithOnApp(businessId)
    }
}