package eka.dr.intl.patients.di

import androidx.room.Room
import eka.dr.intl.patients.data.local.db.DoctorDatabase
import eka.dr.intl.patients.data.local.db.DoctorDatabase.Companion.DATABASE_NAME
import eka.dr.intl.patients.data.repository.DoctorStoreRepositoryImpl
import eka.dr.intl.patients.data.repository.HistoryStoreRepositoryImpl
import eka.dr.intl.patients.data.repository.PatientDirectoryRepositoryImpl
import eka.dr.intl.patients.data.repository.PatientStoreRepositoryImpl
import eka.dr.intl.patients.di.PatientDirectoryKoin.Companion.DOCTOR_STORE_REPOSITORY_IMPL
import eka.dr.intl.patients.di.PatientDirectoryKoin.Companion.GET_PATIENT_COUNT_USE_CASE
import eka.dr.intl.patients.di.PatientDirectoryKoin.Companion.GET_PATIENT_DIRECTORY_BY_ON_APP_USE_CASE
import eka.dr.intl.patients.di.PatientDirectoryKoin.Companion.GET_PATIENT_DIRECTORY_BY_UHID_USE_CASE
import eka.dr.intl.patients.di.PatientDirectoryKoin.Companion.GET_PATIENT_DIRECTORY_USER_CASE
import eka.dr.intl.patients.di.PatientDirectoryKoin.Companion.GET_SEARCH_PATIENT_USE_CASE
import eka.dr.intl.patients.di.PatientDirectoryKoin.Companion.HISTORY_STORE_REPOSITORY_IMPL
import eka.dr.intl.patients.di.PatientDirectoryKoin.Companion.PATIENT_DATABASE_MODULE
import eka.dr.intl.patients.di.PatientDirectoryKoin.Companion.PATIENT_DIRECTORY_REPOSITORY_IMPL
import eka.dr.intl.patients.di.PatientDirectoryKoin.Companion.PATIENT_STORE_REPOSITORY_IMPL
import eka.dr.intl.patients.di.PatientDirectoryKoin.Companion.SYNC_ADD_AND_UPDATE_PATIENT_FROM_LOCAL_USE_CASE
import eka.dr.intl.patients.domain.repository.DoctorStoreRepository
import eka.dr.intl.patients.domain.repository.HistoryStoreRepository
import eka.dr.intl.patients.domain.repository.PatientDirectoryRepository
import eka.dr.intl.patients.domain.repository.PatientStoreRepository
import eka.dr.intl.patients.domain.usecase.GetPatientCountUseCase
import eka.dr.intl.patients.domain.usecase.GetPatientDirectoryByOnAppUseCase
import eka.dr.intl.patients.domain.usecase.GetPatientDirectoryByUhidUseCase
import eka.dr.intl.patients.domain.usecase.GetPatientDirectoryUseCase
import eka.dr.intl.patients.domain.usecase.GetSearchPatientUseCase
import eka.dr.intl.patients.domain.usecase.SyncAddAndUpdatePatientFromLocalUseCase
import eka.dr.intl.patients.presentation.viewModels.AddPatientViewModel
import eka.dr.intl.patients.presentation.viewModels.PatientDirectoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val databaseModule = module {
    single<DoctorDatabase>(named(PATIENT_DATABASE_MODULE)) {
        Room.databaseBuilder(
            get(),
            DoctorDatabase::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }
}

val patientDirectoryModule = module {
    viewModel {
        PatientDirectoryViewModel(
            get(),
            get(named(PATIENT_STORE_REPOSITORY_IMPL)),
            get(named(PATIENT_DIRECTORY_REPOSITORY_IMPL)),
            get(named(GET_PATIENT_DIRECTORY_USER_CASE)),
            get(named(GET_PATIENT_DIRECTORY_BY_ON_APP_USE_CASE)),
            get(named(GET_PATIENT_DIRECTORY_BY_UHID_USE_CASE)),
            get(named(GET_PATIENT_COUNT_USE_CASE)),
            get(named(GET_SEARCH_PATIENT_USE_CASE)),
            get(named(DOCTOR_STORE_REPOSITORY_IMPL))
        )
    }
    viewModel {
        AddPatientViewModel(
            get(),
            get(named(PATIENT_DIRECTORY_REPOSITORY_IMPL)),
        )
    }
}

private val domainModule = module {
    single<PatientStoreRepository>(named(PATIENT_STORE_REPOSITORY_IMPL)) {
        PatientStoreRepositoryImpl(get(named(PATIENT_DATABASE_MODULE)))
    }
    single<DoctorStoreRepository>(named(DOCTOR_STORE_REPOSITORY_IMPL)) {
        DoctorStoreRepositoryImpl(get(named(PATIENT_DATABASE_MODULE)))
    }
    single<PatientDirectoryRepository>(named(PATIENT_DIRECTORY_REPOSITORY_IMPL)) {
        PatientDirectoryRepositoryImpl(
            get(named(HISTORY_STORE_REPOSITORY_IMPL)),
            get(named(PATIENT_STORE_REPOSITORY_IMPL)),
        )
    }
    single<HistoryStoreRepository>(named(HISTORY_STORE_REPOSITORY_IMPL)) {
        HistoryStoreRepositoryImpl(get(named(PATIENT_DATABASE_MODULE)))
    }

    factory(named(GET_PATIENT_DIRECTORY_USER_CASE)) {
        GetPatientDirectoryUseCase(
            get(named(PATIENT_STORE_REPOSITORY_IMPL)),
        )
    }

    factory(named(GET_SEARCH_PATIENT_USE_CASE)) {
        GetSearchPatientUseCase(
            get(named(PATIENT_STORE_REPOSITORY_IMPL)),
        )
    }

    factory(named(GET_PATIENT_DIRECTORY_BY_ON_APP_USE_CASE)) {
        GetPatientDirectoryByOnAppUseCase(
            get(named(PATIENT_STORE_REPOSITORY_IMPL))
        )
    }

    factory(named(GET_PATIENT_COUNT_USE_CASE)) {
        GetPatientCountUseCase(
            get(named(PATIENT_STORE_REPOSITORY_IMPL))
        )
    }

    factory(named(GET_PATIENT_DIRECTORY_BY_UHID_USE_CASE)) {
        GetPatientDirectoryByUhidUseCase(
            get(named(PATIENT_STORE_REPOSITORY_IMPL))
        )
    }

    factory(named(SYNC_ADD_AND_UPDATE_PATIENT_FROM_LOCAL_USE_CASE)) {
        SyncAddAndUpdatePatientFromLocalUseCase(
            get(named(PATIENT_DIRECTORY_REPOSITORY_IMPL))
        )
    }
}


private val listOfPatientDirectoryModule =
    listOf(domainModule, patientDirectoryModule, databaseModule)

fun getPatientDirectoryModules() = listOfPatientDirectoryModule