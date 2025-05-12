package eka.dr.intl.presentation.viewModel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import eka.care.documents.Document
import eka.dr.intl.EkaApp
import eka.dr.intl.common.IAmCommon
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.Resource
import eka.dr.intl.common.Urls.Companion.DOC_PROFILE_URL
import eka.dr.intl.common.UserSharedPref
import eka.dr.intl.common.data.dto.response.UserTokenData
import eka.dr.intl.data.local.db.WorkspaceDatabase
import eka.dr.intl.data.repository.HubRepositoryImpl
import eka.dr.intl.di.DatabaseKoin
import eka.dr.intl.di.ExploreEkaKoin.Companion.UPDATE_SPECIALISATION_WORKSPACE_USE_CASE
import eka.dr.intl.domain.repository.HomeRepository
import eka.dr.intl.domain.usecase.FetchHomeUseCase
import eka.dr.intl.domain.usecase.UpdateSpecialisationWorkspaceUseCase
import eka.dr.intl.patients.data.local.db.DoctorDatabase
import eka.dr.intl.patients.data.local.entity.DoctorEntity
import eka.dr.intl.patients.di.PatientDirectoryKoin
import eka.dr.intl.patients.di.PatientDirectoryKoin.Companion.DOCTOR_STORE_REPOSITORY_IMPL
import eka.dr.intl.patients.di.PatientDirectoryKoin.Companion.SYNC_ADD_AND_UPDATE_PATIENT_FROM_LOCAL_USE_CASE
import eka.dr.intl.patients.domain.repository.DoctorStoreRepository
import eka.dr.intl.patients.domain.repository.PatientDirectoryRepository
import eka.dr.intl.patients.domain.usecase.SyncAddAndUpdatePatientFromLocalUseCase
import eka.dr.intl.patients.presentation.state.UiState
import eka.dr.intl.presentation.states.HomeBottomSheetState
import eka.dr.intl.presentation.states.HomeScreenUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import kotlin.system.exitProcess

class HomeViewModel(app: Application) : AndroidViewModel(app), KoinComponent {
    private val hubRepository = HubRepositoryImpl()
    var homeBottomSheetState = mutableStateOf<HomeBottomSheetState?>(null)
    private val db by inject<DoctorDatabase>(
        named(DatabaseKoin.PATIENT_DATABASE_MODULE)
    )
    private val workspaceDb by inject<WorkspaceDatabase>(
        named(DatabaseKoin.WORKSPACE_DATABASE_MODULE)
    )
    private val doctorStoreRepository by inject<DoctorStoreRepository>(
        named(
            DOCTOR_STORE_REPOSITORY_IMPL
        )
    )
    private val updateSpecialisationWorkspaceUseCase by inject<UpdateSpecialisationWorkspaceUseCase>(
        named(UPDATE_SPECIALISATION_WORKSPACE_USE_CASE)
    )

    private val _error = MutableLiveData<String>()
    var error = _error

    private val fetchHomeUseCase = FetchHomeUseCase()

    private var _homeScreenUiState =
        MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.StateLoading)
    val homeScreenUiState = _homeScreenUiState

    private val _userInformation = MutableStateFlow<UserTokenData?>(null)
    val userInformation = _userInformation.asStateFlow()

    fun updateUserTokenData() {
        viewModelScope.launch {
            _userInformation.value = OrbiUserManager.getUserTokenData()
        }
    }

    fun syncAddAndUpdatePatientFromLocal() {
        val syncAddAndUpdatePatientFromLocalUseCase by inject<SyncAddAndUpdatePatientFromLocalUseCase>(
            named(SYNC_ADD_AND_UPDATE_PATIENT_FROM_LOCAL_USE_CASE)
        )
        syncAddAndUpdatePatientFromLocalUseCase().onEach { result ->
            when (result) {
                is Resource.Loading -> {}
                is Resource.Error -> {}
                is Resource.Success -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun fetchHome() {
        val source = EkaApp.instance.sessionData["utm_source"] ?: "direct"
        val campaign = EkaApp.instance.sessionData["utm_campaign"] ?: "none"
        val medium = EkaApp.instance.sessionData["utm_medium"] ?: "none"
        fetchHomeUseCase(
            source = source,
            campaign = campaign,
            medium = medium,
        ).onEach {
            when (it) {
                is Resource.Error -> {
                    _homeScreenUiState.emit(HomeScreenUiState.StateError("Something went wrong"))
                }

                is Resource.Loading -> {
                    _homeScreenUiState.emit(HomeScreenUiState.StateLoading)
                }

                is Resource.Success -> {
                    _homeScreenUiState.emit(
                        HomeScreenUiState.StateSuccess(
                            it.data
                        )
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun clearAllTables() {
        viewModelScope.launch(Dispatchers.IO) {
            Document.destroy()
            workspaceDb.clearAllTables()
            db.clearAllTables()
        }
    }

    private suspend fun insertAllDoctors(doctors: List<DoctorEntity>) {
        withContext(Dispatchers.IO) {
            db.doctorDao().insertAllDoctors(*doctors.toTypedArray())
        }
    }

    private var _patientDirectorySyncStatus = MutableStateFlow<UiState<Boolean>>(UiState())
    val patientDirectorySyncStatus = _patientDirectorySyncStatus.asStateFlow()

    //listOfDoctors
    private val _listOfDoctors = MutableStateFlow(emptyList<DoctorEntity>())
    val listOfDoctors = _listOfDoctors.asStateFlow()


    fun getListOfDoctors() {
        viewModelScope.launch(Dispatchers.IO) {
            val doctors = doctorStoreRepository.getAllDoctors()
            _listOfDoctors.value = doctors
        }
    }

    fun getUserConfiguration(context: Context) {
        viewModelScope.launch {
            try {
                _patientDirectorySyncStatus.emit(UiState(loading = true))
                val loggedInUserOid = OrbiUserManager.getUserTokenData()?.oid ?: ""
                val response = hubRepository.getAccountConfiguration()
                if (response == null) {
                    _patientDirectorySyncStatus.emit(UiState(loading = false))
                    return@launch
                }

                EkaApp.setValue(
                    "isAbha",
                    response?.abha == true
                )

                val businessId =
                    OrbiUserManager.getUserTokenData()?.businessId
                        ?: "b-${loggedInUserOid}"

                // if logged in user oid is present in doctors list id then it's a doctor else staff
                val isDoctor = response?.doctors?.any { it.id == loggedInUserOid } ?: false
                OrbiUserManager.saveSelectedBusiness(businessId)

                EkaApp.setValue(
                    "$businessId-groups",
                    Gson().toJson(response.groups) ?: "[]"
                )

                updateSpecialisationWorkspaceUseCase.invoke(
                    bid = businessId,
                    oid = loggedInUserOid,
                    specialisation = response?.businessName ?: ""
                )

                val doctors = response.doctors.map {
                    val speciality = it?.professional?.majorSpeciality?.name
                    DoctorEntity(
                        id = it.id,
                        fln = "${it.personal.name.f} ${it.personal.name.l}",
                        gender = it.personal.gender,
                        dob = it.personal.dob,
                        pic = DOC_PROFILE_URL + it.id,
                        email = it.personal.email,
                        businessId = businessId,
                        specialisation = speciality,
                    )
                }

                if (isDoctor) {
                    OrbiUserManager.saveSelectedDoctorId(loggedInUserOid)
                } else {
                    if (doctors.size == 1) {
                        OrbiUserManager.saveSelectedDoctorId(doctors.first().id)
                    } else {
                        OrbiUserManager.saveSelectedDoctorId(null)
                    }
                }

                insertAllDoctors(doctors)
                _patientDirectorySyncStatus.emit(UiState(loading = false))
            } catch (ex: Exception) {
                _patientDirectorySyncStatus.emit(UiState(loading = false))
                _error.postValue(ex.localizedMessage)
            }

        }
    }

    fun logout(dataRetained: Boolean) {
        if (!dataRetained) {
            clearAllTables()
        }
        UserSharedPref(EkaApp.instance).logoutUser()

        exitProcess(0)
    }
}