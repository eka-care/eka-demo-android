package eka.dr.intl.patients.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import eka.dr.intl.common.IAmCommon
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.utility.DateUtils
import eka.dr.intl.common.utility.DateUtils.Companion.convertLongToDateString
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.patients.data.local.entity.FormDataEntity
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.data.remote.dto.response.GetFormFieldsResponse
import eka.dr.intl.patients.presentation.components.form.AgeOrDobInput
import eka.dr.intl.patients.presentation.components.form.AlternatePhoneInput
import eka.dr.intl.patients.presentation.components.form.BloodGroupInput
import eka.dr.intl.patients.presentation.components.form.ChannelInput
import eka.dr.intl.patients.presentation.components.form.EmailIdInput
import eka.dr.intl.patients.presentation.components.form.GenderInput
import eka.dr.intl.patients.presentation.components.form.GuardianNameInput
import eka.dr.intl.patients.presentation.components.form.LocationForm
import eka.dr.intl.patients.presentation.components.form.MaritalStatusInput
import eka.dr.intl.patients.presentation.components.form.MobileNumberInput
import eka.dr.intl.patients.presentation.components.form.NameInput
import eka.dr.intl.patients.presentation.components.form.NameOfInformantInput
import eka.dr.intl.patients.presentation.components.form.PatientAddressInput
import eka.dr.intl.patients.presentation.components.form.PatientOccupationInput
import eka.dr.intl.patients.presentation.components.form.ReferredByInput
import eka.dr.intl.patients.presentation.components.form.RelationshipWithPatientInput
import eka.dr.intl.patients.presentation.components.form.UhidInput
import eka.dr.intl.patients.presentation.viewModels.AddPatientViewModel
import eka.dr.intl.patients.utils.DynamicFormKeys
import eka.dr.intl.patients.utils.indianCities
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPatientScreenContent(padding: PaddingValues) {
    val context = LocalContext.current
    val state = rememberLazyListState()
    val viewModel: AddPatientViewModel = koinViewModel()
    val businessId = OrbiUserManager.getSelectedBusiness() ?: ""
    val formFields = Gson().fromJson(
        (context.applicationContext as IAmCommon).getValue("$businessId-formFields", "[]"),
        Array<GetFormFieldsResponse.Form.AddPatientEntity>::class.java
    ).toList()

    val isPhoneCountryCodeEnabled =
        formFields.find { it.key == DynamicFormKeys.PHONE_COUNTRY_CODE.type }?.key?.isEmpty() == false

    val isSalutationEnabled =
        formFields.find { it.key == DynamicFormKeys.SALUTATION.type }?.key?.isEmpty() == false

    val isCityEnabled =
        formFields.find { it.key == DynamicFormKeys.CITY.type }?.key?.isEmpty() == false
    val isPincodeEnabled =
        formFields.find { it.key == DynamicFormKeys.PINCODE.type }?.key?.isEmpty() == false


    LazyColumn(
        state = state,
        contentPadding = padding,
        modifier = Modifier
            .fillMaxSize()
            .background(DarwinTouchNeutral0),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item(key = "profile_picture") {
            Spacer(Modifier.height(16.dp))
            ProfilePictureUpload()
            Spacer(Modifier.height(16.dp))
        }
        item(key = "mobile_number") {
            MobileNumberInput(
                isPhoneCountryCodeEnabled = isPhoneCountryCodeEnabled,
                onCodeChange = { viewModel.updateCode(it) },
                code = viewModel.code.collectAsState().value,
                searchValue = viewModel.mobileNumber.collectAsState().value,
                onSearchValueChange = { viewModel.updateMobileNumber(it) }
            )
        }
        item(key = "name") {
            NameInput(
                name = viewModel.patientName.collectAsState().value,
                onNameChange = { viewModel.updatePatientName(it) },
                salutation = viewModel.salutation.collectAsState().value,
                onSalutationChange = { viewModel.updateSalutation(it) },
                isSalutationEnabled = isSalutationEnabled
            )
        }
        item(key = "age_or_dob") {
            AgeOrDobInput(
                age = viewModel.age.collectAsState().value,
                dateOfBirth = viewModel.dob.collectAsState().value,
                onDateOfBirthChange = { viewModel.updateDob(it) },
                viewModel,
            )
        }
        item(key = "gender") {
            GenderInput(
                selectedOption = viewModel.gender.collectAsState().value,
                onOptionSelected = {
                    viewModel.updateGender(it)
                }
            )
        }
        item(key = "uhid") {
            UhidInput(
                value = viewModel.uhid.collectAsState().value,
                onValueChange = { viewModel.updateUhid(it) },
                onGenerateClick = {
                    viewModel.generateUhid()
                }
            )
        }
        item("location") {
            LocationForm(
                isCityEnabled = isCityEnabled,
                isPincodeEnabled = isPincodeEnabled,
                selectedCity = viewModel.city.collectAsState().value,
                pincode = viewModel.pincode.collectAsState().value,
                onCityChange = { viewModel.updateCity(it) },
                onPincodeChange = { viewModel.updatePincode(it) },
                listOfCities = indianCities,
            )
        }

        formFields?.forEach {
            when (it.key) {
                DynamicFormKeys.REFERRED_BY_DOCTOR.type -> {
                    item {
                        ReferredByInput(
                            referredBy = viewModel.referredByDoctor.collectAsState().value,
                            onReferredByChange = { viewModel.updateReferredByDoctor(it) },
                            referredByContact = viewModel.referredByDoctorContact.collectAsState().value,
                            onReferredByContactChange = {
                                viewModel.updateReferredByDoctorContact(
                                    it
                                )
                            },
                        )
                    }
                }

                DynamicFormKeys.PATIENT_ADDRESS.type -> {
                    item {
                        PatientAddressInput(
                            address = viewModel.address.collectAsState().value,
                            onAddressChange = { viewModel.updateAddress(it) }
                        )
                    }
                }

                DynamicFormKeys.EMAIL.type -> {
                    item {
                        EmailIdInput(
                            emailId = viewModel.emailId.collectAsState().value,
                            onEmailIdChange = { viewModel.updateEmailId(it) }
                        )
                    }
                }

                DynamicFormKeys.BLOOD_GROUP.type -> {
                    item {
                        BloodGroupInput(
                            bloodGroup = viewModel.bloodGroup.collectAsState().value,
                            onBloodGroupChange = { viewModel.updateBloodGroup(it) }
                        )
                    }
                }

                DynamicFormKeys.ALTERNATIVE_PHONE.type -> {
                    item {
                        AlternatePhoneInput(
                            value = viewModel.alternateContact.collectAsState().value,
                            onValueChange = { viewModel.updateAlternateContact(it) }
                        )
                    }
                }


                DynamicFormKeys.MARITAL_STATUS.type -> {
                    item {
                        MaritalStatusInput(
                            maritalStatus = viewModel.maritalStatus.collectAsState().value,
                            onMaritalStatusChange = { viewModel.updateMaritalStatus(it) },
                            listOfMaritalStatus = listOf(
                                "Single",
                                "Married",
                                "Divorced",
                                "Widowed",
                                "Separated",
                                "Unknown"
                            )
                        )
                    }
                }

                DynamicFormKeys.NAME_OF_INFORMANT.type -> {
                    item {
                        NameOfInformantInput(
                            name = viewModel.nameOfInformant.collectAsState().value,
                            onNameChange = { viewModel.updateNameOfInformant(it) }
                        )
                    }
                }

                DynamicFormKeys.CHANNEL.type -> {
                    item {
                        ChannelInput(
                            channel = viewModel.channel.collectAsState().value,
                            onChannelChange = { viewModel.updateChannel(it) }
                        )
                    }
                }

                DynamicFormKeys.PATIENT_OCCUPATION.type -> {
                    item {
                        PatientOccupationInput(
                            value = viewModel.patientOccupation.collectAsState().value,
                            onValueChange = { viewModel.updatePatientOccupation(it) }
                        )
                    }
                }

                DynamicFormKeys.GUARDIAN_NAME.type -> {
                    item {
                        GuardianNameInput(
                            name = viewModel.guardiansName.collectAsState().value,
                            onNameChange = { viewModel.updateGuardiansName(it) }
                        )
                    }
                }

                DynamicFormKeys.RELATIONSHIP_WITH_PATIENT.type -> {
                    item {
                        RelationshipWithPatientInput(
                            selectedRelationship = viewModel.relationshipWithPatient.collectAsState().value,
                            onRelationshipChange = {
                                viewModel.updateRelationshipWithPatient(
                                    it
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

fun handleAddPatient(
    patient: PatientEntity,
    viewModel: AddPatientViewModel,
) {
    viewModel.updatePatientOid(patient.oid)
    viewModel.updatePatientName(patient.name)
    val regex = Regex("[^0-9]")
    val code = regex.replace(patient.countryCode?.toString() ?: "", "")
    if (code.isEmpty()) {
        viewModel.updateCode("+91")
    } else {
        viewModel.updateCode("+$code")
    }
    viewModel.updateMobileNumber(patient.phone?.toString() ?: "")
    val dobFormatted = patient.age?.let {
        convertLongToDateString(it).split("-").reversed().joinToString("/")
    }
    val age = patient.age?.let {
        DateUtils.getAgeFromYYYYMMDDNumber(convertLongToDateString(it))
    }
    viewModel.updateAge(age)
    viewModel.updateOnApp(patient.onApp == true)
    viewModel.updateUuid(patient.uuid ?: "")
    viewModel.updateReferredBy(patient.referredBy ?: "")
    viewModel.updateReferId(patient.referId ?: "")

    viewModel.updateDob(dobFormatted ?: "")
    viewModel.updateGender(patient.gender.value)
    viewModel.updateBloodGroup(patient.bloodGroup?.value ?: "")

    val formData = Gson().fromJson(
        patient.formData,
        Array<FormDataEntity>::class.java
    )

    val uhid =
        formData.find { it.key == "uhid" }?.value as String?
    viewModel.updateUhid(uhid ?: "")

    formData.forEach {
        when (it.key) {
            DynamicFormKeys.SALUTATION.type -> {
                val salutation =
                    formData.find { curr -> curr.key == DynamicFormKeys.SALUTATION.type }?.value as String?
                viewModel.updateSalutation((salutation ?: ""))
            }

            DynamicFormKeys.CITY.type -> {
                val city =
                    formData.find { curr -> curr.key == DynamicFormKeys.CITY.type }?.value as String?
                viewModel.updateCity((city ?: ""))
            }

            DynamicFormKeys.PINCODE.type -> {
                val pincode =
                    formData.find { curr -> curr.key == DynamicFormKeys.PINCODE.type }?.value as String?
                if (pincode.isNullOrEmpty()) {
                    viewModel.updatePincode("")
                } else {
                    viewModel.updatePincode(pincode)
                }
            }

            DynamicFormKeys.EMAIL.type -> {
                val email =
                    formData.find { curr -> curr.key == DynamicFormKeys.EMAIL.type }?.value as String?
                viewModel.updateEmailId(email ?: "")
            }

            DynamicFormKeys.ALTERNATIVE_PHONE.type -> {
                val alternateContact =
                    formData.find { curr -> curr.key == DynamicFormKeys.ALTERNATIVE_PHONE.type }?.value as String?
                viewModel.updateAlternateContact(alternateContact ?: "")
            }

            DynamicFormKeys.REFERRED_BY_DOCTOR.type -> {
                val referredByDoctor =
                    formData.find { curr -> curr.key == DynamicFormKeys.REFERRED_BY_DOCTOR.type }?.value as String?
                viewModel.updateReferredByDoctor(referredByDoctor ?: "")
            }

            DynamicFormKeys.PATIENT_ADDRESS.type -> {
                val address =
                    formData.find { curr -> curr.key == DynamicFormKeys.PATIENT_ADDRESS.type }?.value as String?
                viewModel.updateAddress(address ?: "")
            }

            DynamicFormKeys.MARITAL_STATUS.type -> {
                val maritalStatus =
                    formData.find { curr -> curr.key == DynamicFormKeys.MARITAL_STATUS.type }?.value as String?
                viewModel.updateMaritalStatus(maritalStatus ?: "")
            }

            DynamicFormKeys.NAME_OF_INFORMANT.type -> {
                val nameOfInformant =
                    formData.find { curr -> curr.key == DynamicFormKeys.NAME_OF_INFORMANT.type }?.value as String?
                viewModel.updateNameOfInformant(nameOfInformant ?: "")
            }

            DynamicFormKeys.CHANNEL.type -> {
                val channel =
                    formData.find { curr -> curr.key == DynamicFormKeys.CHANNEL.type }?.value as String?
                viewModel.updateChannel(channel ?: "")
            }

            DynamicFormKeys.PATIENT_OCCUPATION.type -> {
                val patientOccupation =
                    formData.find { curr -> curr.key == DynamicFormKeys.PATIENT_OCCUPATION.type }?.value as String?
                viewModel.updatePatientOccupation(patientOccupation ?: "")
            }

            DynamicFormKeys.GUARDIAN_NAME.type -> {
                val guardiansName =
                    formData.find { curr -> curr.key == DynamicFormKeys.GUARDIAN_NAME.type }?.value as String?
                viewModel.updateGuardiansName(guardiansName ?: "")
            }

            DynamicFormKeys.RELATIONSHIP_WITH_PATIENT.type -> {
                val relationshipWithPatient =
                    formData.find { curr -> curr.key == DynamicFormKeys.RELATIONSHIP_WITH_PATIENT.type }?.value as String?
                viewModel.updateRelationshipWithPatient(relationshipWithPatient ?: "")
            }
        }
    }
}

