package eka.dr.intl.patients.di

class PatientDirectoryKoin {
    companion object {
        const val PATIENT_DATABASE_MODULE = "patientDatabaseModule"
        const val PATIENT_DIRECTORY_REPOSITORY_IMPL = "PatientDirectoryRepositoryImpl"
        const val PATIENT_STORE_REPOSITORY_IMPL = "PatientStoreRepositoryImpl"
        const val HISTORY_STORE_REPOSITORY_IMPL = "HistoryStoreRepositoryImpl"
        const val MEDICAL_HISTORY_STORE_REPOSITORY_IMPL = "MedicalHistoryStoreRepositoryImpl"
        const val DOCTOR_STORE_REPOSITORY_IMPL = "DoctorStoreRepositoryImplForPatient"
        const val GET_PATIENT_DIRECTORY_USER_CASE = "GetPatientDirectoryUserCase"
        const val GET_PATIENT_DIRECTORY_BY_ON_APP_USE_CASE = "GetPatientDirectoryByOnAppUseCase"
        const val GET_PATIENT_DIRECTORY_BY_UHID_USE_CASE = "GetPatientDirectoryByUhidUseCase"
        const val GET_PATIENT_COUNT_USE_CASE = "GetPatientCountUseCase"
        const val UPSERT_PATIENT_DIRECTORY_USE_CASE = "UpsertPatientDirectoryUseCase"
        const val GET_PATIENT_COUNT_ON_APP_USE_CASE = "GetPatientCountOnAppUseCase"
        const val ADD_PATIENT_VIEW_MODEL = "AddPatientViewModel"
        const val GET_SEARCH_PATIENT_USE_CASE = "GetSearchPatientUseCase"
        const val SYNC_ADD_AND_UPDATE_PATIENT_FROM_LOCAL_USE_CASE =
            "SyncAddAndUpdatePatientFromLocalUseCase"
        const val CLINIC_STORE_REPOSITORY_IMPL = "ClinicStoreRepositoryImpl"
    }
}