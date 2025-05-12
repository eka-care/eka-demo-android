package eka.dr.intl.patients.domain.repository

import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.data.remote.dto.request.AddPatientRequest
import eka.dr.intl.patients.data.remote.dto.request.UpdatePatientRequest
import eka.dr.intl.patients.data.remote.dto.response.AddPatientResponse
import eka.dr.intl.patients.data.remote.dto.response.ArchivePatientResponse
import eka.dr.intl.patients.data.remote.dto.response.GenerateUHIDResponse
import eka.dr.intl.patients.data.remote.dto.response.GetFormFieldsResponse
import eka.dr.intl.patients.data.remote.dto.response.GetPatientByIdResponse
import eka.dr.intl.patients.data.remote.dto.response.PatientDirectoryResponse


interface PatientDirectoryRepository {
    suspend fun getPatientDirectory(bid: String): PatientDirectoryResponse?
    suspend fun archivePatient(patient: PatientEntity): ArchivePatientResponse?
    suspend fun addPatientToDirectory(
        patient: AddPatientRequest,
    ): AddPatientResponse?

    suspend fun updatePatientInDirectory(
        patientOid: String,
        patient: UpdatePatientRequest,
    ): AddPatientResponse?

    suspend fun generateUHID(): GenerateUHIDResponse?
    suspend fun getFormFields(): GetFormFieldsResponse?
    suspend fun syncAddAndUpdatePatientFromLocal(): Boolean?
    suspend fun addOrUpdatePatientToDirectory(
        patient: PatientEntity,
        update: Boolean
    ): String?

    suspend fun getPatientById(patientOid: String): GetPatientByIdResponse?
}