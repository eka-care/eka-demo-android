package eka.dr.intl.patients.presentation.viewModels

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eka.dr.intl.common.Resource
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.data.remote.dto.response.AddPatientToDirectoryResponse
import eka.dr.intl.patients.data.remote.dto.response.GenerateUHIDResponse
import eka.dr.intl.patients.domain.repository.PatientDirectoryRepository
import eka.dr.intl.patients.domain.usecase.AddPatientToDirectoryUseCase
import eka.dr.intl.patients.domain.usecase.GenerateUhidUseCase
import eka.dr.intl.patients.presentation.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AddPatientViewModel(
    private val app: Application,
    patientDirectoryRepository: PatientDirectoryRepository,
) : ViewModel() {
    //favourite
    private val _favourite = MutableStateFlow(false)
    val favourite = _favourite.asStateFlow()

    //showAgePickerBottomSheet
    private val _showAgePickerBottomSheet = MutableStateFlow(false)
    val showAgePickerBottomSheet = _showAgePickerBottomSheet.asStateFlow()
    fun updateShowAgePickerBottomSheet(showAgePickerBottomSheet: Boolean) {
        _showAgePickerBottomSheet.value = showAgePickerBottomSheet
    }

    //    uuid
    private val _uuid = MutableStateFlow("")
    val uuid = _uuid.asStateFlow()
    fun updateUuid(uuid: String) {
        _uuid.value = uuid
    }

    //    referredBy
    private val _referredBy = MutableStateFlow("")
    val referredBy = _referredBy.asStateFlow()
    fun updateReferredBy(referredBy: String) {
        _referredBy.value = referredBy
    }

    //    referId
    private val _referId = MutableStateFlow("")
    val referId = _referId.asStateFlow()
    fun updateReferId(referId: String) {
        _referId.value = referId
    }

    //    followUp
    private val _followUp = MutableStateFlow("")
    val followUp = _followUp.asStateFlow()
    fun updateFollowUp(followUp: String) {
        _followUp.value = followUp
    }

    //    lastVisit
    private val _lastVisit = MutableStateFlow("")
    val lastVisit = _lastVisit.asStateFlow()
    fun updateLastVisit(lastVisit: String) {
        _lastVisit.value = lastVisit
    }

    // onApp
    private val _onApp = MutableStateFlow(false)
    val onApp = _onApp.asStateFlow()
    fun updateOnApp(onApp: Boolean) {
        _onApp.value = onApp
    }

    private val _generateUhid =
        MutableStateFlow<UiState<GenerateUHIDResponse>>(UiState())
    val generateUhid = _generateUhid.asStateFlow()

    private val generateUhidUseCase = GenerateUhidUseCase(patientDirectoryRepository)
    private val addPatientToDirectoryUseCase =
        AddPatientToDirectoryUseCase(patientDirectoryRepository)

    //patientOid
    private val _patientOid = MutableStateFlow<String?>(null)
    val patientOid = _patientOid.asStateFlow()
    fun updatePatientOid(patientOid: String?) {
        _patientOid.value = patientOid
    }

    // abhaId
    private val _abhaId = MutableStateFlow<String?>(null)
    val abhaId = _abhaId.asStateFlow()
    fun updateAbhaId(abhaId: String?) {
        _abhaId.value = abhaId
    }

    private val _code = MutableStateFlow("+91")
    val code = _code.asStateFlow()
    fun updateCode(code: String) {
        _code.value = code
    }

    private val _mobileNumber = MutableStateFlow("")
    val mobileNumber = _mobileNumber.asStateFlow()
    fun updateMobileNumber(mobileNumber: String) {
        _mobileNumber.value = mobileNumber
    }

    //address
    private val _address = MutableStateFlow<String>("")
    val address = _address.asStateFlow()
    fun updateAddress(address: String) {
        _address.value = address
    }

    //patient name
    private val _patientName = MutableStateFlow<String>("")
    val patientName = _patientName.asStateFlow()
    fun updatePatientName(patientName: String) {
        _patientName.value = patientName
    }

    //salutation
    private val _salutation = MutableStateFlow<String>("")
    val salutation = _salutation.asStateFlow()
    fun updateSalutation(salutation: String) {
        _salutation.value = salutation
    }

    private val _age = MutableStateFlow<Long?>(null)
    val age = _age.asStateFlow()
    var selectedYear by mutableIntStateOf(0)
    var selectedMonth by mutableIntStateOf(0)
    var selectedDay by mutableIntStateOf(0)
    fun updateAge(age: Long?) {
        _age.value = age
    }

    private val _dob = MutableStateFlow("")
    val dob = _dob.asStateFlow()
    fun updateDob(dob: String) {
        _dob.value = dob
    }

    private val _gender = MutableStateFlow("")
    val gender = _gender.asStateFlow()
    fun updateGender(gender: String) {
        _gender.value = gender
    }

    // uhid
    private val _uhid = MutableStateFlow("")
    val uhid = _uhid.asStateFlow()
    fun updateUhid(uhid: String) {
        _uhid.value = uhid
    }

    //alternate contact
    private val _alternateContact = MutableStateFlow("")
    val alternateContact = _alternateContact.asStateFlow()
    fun updateAlternateContact(alternateContact: String) {
        _alternateContact.value = alternateContact
    }

    //pincode
    private val _pincode = MutableStateFlow("")
    val pincode = _pincode.asStateFlow()
    fun updatePincode(pincode: String) {
        _pincode.value = pincode
    }

    //city
    private val _city = MutableStateFlow("")
    val city = _city.asStateFlow()
    fun updateCity(city: String) {
        _city.value = city
    }

    //referred by doctor
    private val _referredByDoctor = MutableStateFlow("")
    val referredByDoctor = _referredByDoctor.asStateFlow()
    fun updateReferredByDoctor(referredByDoctor: String) {
        _referredByDoctor.value = referredByDoctor
    }

    //referred by doctors contact
    private val _referredByDoctorContact = MutableStateFlow("")
    val referredByDoctorContact = _referredByDoctorContact.asStateFlow()
    fun updateReferredByDoctorContact(referredByDoctorContact: String) {
        _referredByDoctorContact.value = referredByDoctorContact
    }

    //relationship with patient
    private val _relationshipWithPatient = MutableStateFlow("")
    val relationshipWithPatient = _relationshipWithPatient.asStateFlow()
    fun updateRelationshipWithPatient(relationshipWithPatient: String) {
        _relationshipWithPatient.value = relationshipWithPatient
    }

    //guardians name
    private val _guardiansName = MutableStateFlow("")
    val guardiansName = _guardiansName.asStateFlow()
    fun updateGuardiansName(guardiansName: String) {
        _guardiansName.value = guardiansName
    }

    //marital status
    private val _maritalStatus = MutableStateFlow<String>("")
    val maritalStatus = _maritalStatus.asStateFlow()
    fun updateMaritalStatus(maritalStatus: String) {
        _maritalStatus.value = maritalStatus
    }

    private val _bloodGroup = MutableStateFlow<String>("")
    val bloodGroup = _bloodGroup.asStateFlow()
    fun updateBloodGroup(bloodGroup: String) {
        _bloodGroup.value = bloodGroup
    }

    //patient occupation
    private val _patientOccupation = MutableStateFlow<String>("")
    val patientOccupation = _patientOccupation.asStateFlow()
    fun updatePatientOccupation(patientOccupation: String) {
        _patientOccupation.value = patientOccupation
    }

    //name of informant
    private val _nameOfInformant = MutableStateFlow<String>("")
    val nameOfInformant = _nameOfInformant.asStateFlow()
    fun updateNameOfInformant(nameOfInformant: String) {
        _nameOfInformant.value = nameOfInformant
    }

    //email id
    private val _emailId = MutableStateFlow("")
    val emailId = _emailId.asStateFlow()
    fun updateEmailId(emailId: String) {
        _emailId.value = emailId
    }

    //tag
    private val _tag = MutableStateFlow("")
    val tag = _tag.asStateFlow()
    fun updateTag(tag: String) {
        _tag.value = tag
    }

    private val _tags = MutableStateFlow<List<String>>(emptyList())
    val tags = _tags.asStateFlow()
    fun updateTags(tags: List<String>) {
        _tags.value = tags
    }

    //channel
    private val _channel = MutableStateFlow("")
    val channel = _channel.asStateFlow()
    fun updateChannel(channel: String) {
        _channel.value = channel
    }

    fun generateUhid() {
        generateUhidUseCase().onEach {
            when (it) {
                is Resource.Error -> {
                    _generateUhid.emit(UiState(error = it.message))
                    Toast.makeText(app.applicationContext, it.message, Toast.LENGTH_SHORT)
                        .show()
                }

                is Resource.Loading -> {
                    _generateUhid.emit(UiState(loading = true))
                }

                is Resource.Success -> {
                    _generateUhid.emit(UiState(data = it.data))
                    Toast.makeText(
                        app.applicationContext,
                        "UHID Generated Successfully",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    updateUhid(it.data?.uhid ?: "")
                }
            }
        }.launchIn(viewModelScope)
    }

    private val _addPatientToDirectory =
        MutableStateFlow<UiState<AddPatientToDirectoryResponse>>(UiState())
    val addPatientToDirectory = _addPatientToDirectory.asStateFlow()

    fun addPatientToDirectory(patientEntity: PatientEntity) {
        addPatientToDirectoryUseCase(patientEntity).onEach {
            when (it) {
                is Resource.Error -> {
                    _addPatientToDirectory.emit(UiState(error = it.message))
                }

                is Resource.Loading -> {
                    _addPatientToDirectory.emit(UiState(loading = true))
                }

                is Resource.Success -> {
                    _addPatientToDirectory.emit(UiState(data = it.data))
                }
            }
        }.launchIn(viewModelScope)
    }
}