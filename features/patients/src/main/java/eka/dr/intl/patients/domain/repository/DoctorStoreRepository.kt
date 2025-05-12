package eka.dr.intl.patients.domain.repository

import eka.dr.intl.patients.data.local.entity.DoctorEntity

interface DoctorStoreRepository {
    suspend fun getAllDoctors(): List<DoctorEntity>
    suspend fun insetAllDoctors(doctors: List<DoctorEntity>)
}