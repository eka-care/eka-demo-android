package eka.dr.intl.patients.presentation.viewModels

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.Resource
import eka.dr.intl.patients.data.local.entity.DoctorEntity
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.domain.repository.DoctorStoreRepository
import eka.dr.intl.patients.domain.repository.PatientDirectoryRepository
import eka.dr.intl.patients.domain.repository.PatientStoreRepository
import eka.dr.intl.patients.domain.usecase.ArchivePatientUseCase
import eka.dr.intl.patients.domain.usecase.GetPatientCountUseCase
import eka.dr.intl.patients.domain.usecase.GetPatientDirectoryByOnAppUseCase
import eka.dr.intl.patients.domain.usecase.GetPatientDirectoryByUhidUseCase
import eka.dr.intl.patients.domain.usecase.GetPatientDirectoryUseCase
import eka.dr.intl.patients.domain.usecase.GetSearchPatientUseCase
import eka.dr.intl.patients.presentation.sync.enqueuePatientDirectoryWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PatientDirectoryViewModel(
    private val app: Application,
    private val patientStoreRepository: PatientStoreRepository,
    private val patientDirectoryRepository: PatientDirectoryRepository,
    private val getPatientDirectoryUseCase: GetPatientDirectoryUseCase,
    private val getPatientDirectoryByOnAppUseCase: GetPatientDirectoryByOnAppUseCase,
    private val getPatientDirectoryByUhidUseCase: GetPatientDirectoryByUhidUseCase,
    private val getPatientCountUseCase: GetPatientCountUseCase,
    private val getSearchPatientUseCase: GetSearchPatientUseCase,
    private val doctorStoreRepository: DoctorStoreRepository
) : ViewModel() {
    private val _from = MutableStateFlow("")
    val from = _from.asStateFlow()
    fun setFrom(from: String) {
        _from.value = from
    }

    private val archivePatientUseCase = ArchivePatientUseCase(patientDirectoryRepository)

    private val _refreshing = MutableStateFlow<Boolean>(false)
    val refreshing = _refreshing.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _searchResults: MutableStateFlow<PagingData<PatientEntity>> =
        MutableStateFlow(PagingData.empty())
    var searchResults = _searchResults.asStateFlow()

    private val _active = MutableStateFlow(false)
    val active = _active.asStateFlow()

    private val _count = MutableStateFlow(PatientCount())
    val count = _count.asStateFlow()

    private fun loadPatients() {
        getPatientDirectoryUseCase.get()
            .cachedIn(viewModelScope).onEach {
                _searchResults.value = it
            }.launchIn(viewModelScope)

    }

    init {
        loadPatients()
        loadCounts()
    }

    fun setActive(active: Boolean) {
        _active.value = active
    }

    suspend fun getPatientByOid(patientOid: String): PatientEntity {
        return patientStoreRepository.getPatientDataByProfile(patientOid)
    }

    fun loadCounts() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            getPatientCountUseCase.invoke().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _loading.value = true
                    }

                    is Resource.Success -> {
                        _loading.value = false
                        _count.value = result.data ?: PatientCount()
                    }

                    is Resource.Error -> {
                        _loading.value = false
                    }
                }
            }
        }
    }

    fun filterPatients(filter: Filter, tagIds: List<String>? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            when (filter) {
                Filter.ALL -> getPatientDirectoryUseCase.get()
                    .cachedIn(viewModelScope).onEach {
                        _searchResults.value = it
                    }.launchIn(viewModelScope)

                Filter.ON_APP -> getPatientDirectoryByOnAppUseCase.get()
                    .cachedIn(viewModelScope).onEach {
                        _searchResults.value = it
                    }.launchIn(viewModelScope)

                Filter.UHID -> getPatientDirectoryByUhidUseCase.get()
                    .cachedIn(viewModelScope).onEach {
                        _searchResults.value = it
                    }.launchIn(viewModelScope)
            }
        }
    }


    fun archivePatient(patient: PatientEntity, onSuccess: () -> Unit = {}) {
        archivePatientUseCase(patient)
            .flowOn(Dispatchers.IO) // Do repository operations on IO thread
            .onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        // Handle loading state
                    }

                    is Resource.Error -> {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                app.applicationContext,
                                result.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    is Resource.Success -> {
                        withContext(Dispatchers.Main) {
                            onSuccess()
                            loadPatients()
                            loadCounts()
                            Toast.makeText(
                                app.applicationContext,
                                result.message ?: "Success",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            .catch { exception ->
                // Handle any exceptions that might occur in the above operators
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        app.applicationContext,
                        exception.message ?: "Error archiving patient",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .launchIn(viewModelScope)
    }

    fun updatePatientDetails(data: PatientEntity?) {
        viewModelScope.launch(Dispatchers.IO) {
            data?.copy(updatedAt = System.currentTimeMillis())
                ?.let { patientStoreRepository.updatePatientData(it) }
            patientDirectoryRepository.syncAddAndUpdatePatientFromLocal()
        }
    }

    // list of doctors
    private val _doctors = MutableStateFlow<List<DoctorEntity>>(emptyList())
    val doctors = _doctors.asStateFlow()

    suspend fun getAllDoctors(): List<DoctorEntity> {
        return doctorStoreRepository.getAllDoctors()
    }

    fun refreshPatients(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _refreshing.value = true
            val bid = OrbiUserManager.getSelectedBusiness() ?: ""
            enqueuePatientDirectoryWorker(context, bid)
            patientDirectoryRepository.syncAddAndUpdatePatientFromLocal()
            //TODO refresh count
            loadCounts()
            _refreshing.value = false
        }
    }

    fun refreshPatientDirectory() {
        viewModelScope.launch(Dispatchers.IO) {
            val bid = OrbiUserManager.getSelectedBusiness() ?: ""
            patientDirectoryRepository.getPatientDirectory(bid)
        }
    }

    fun searchPatient(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (query.isEmpty()) {
                loadPatients()
                return@launch
            }

            getSearchPatientUseCase.get(query)
                .cachedIn(viewModelScope).onEach {
                    _searchResults.value = it
                }.launchIn(viewModelScope)
        }
    }
}

enum class Filter {
    ALL,
    ON_APP,
    UHID
}

data class PatientCount(
    val allPatientCount: Int = 0,
    var uhidPatientCount: Int = 0,
    var onAppPatientCount: Int = 0,
    val patientNotSyncedCount: Int = 0
)
