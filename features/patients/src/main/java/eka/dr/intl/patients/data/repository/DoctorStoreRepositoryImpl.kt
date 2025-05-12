package eka.dr.intl.patients.data.repository

import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.patients.data.local.db.DoctorDatabase
import eka.dr.intl.patients.data.local.entity.DoctorEntity
import eka.dr.intl.patients.domain.repository.DoctorStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DoctorStoreRepositoryImpl(val db: DoctorDatabase) : DoctorStoreRepository {
    override suspend fun getAllDoctors(): List<DoctorEntity> {
        try {
            val businessId = OrbiUserManager.getSelectedBusiness()
                ?: "b-${OrbiUserManager.getUserTokenData()?.oid}"

            return withContext(Dispatchers.IO) {
                db.doctorDao().getAllDoctors(businessId)
            }
        } catch (e: Exception) {
            return emptyList()
        }
    }

    override suspend fun insetAllDoctors(doctors: List<DoctorEntity>) {
        db.doctorDao().insertAllDoctors(*doctors.toTypedArray())
    }
}