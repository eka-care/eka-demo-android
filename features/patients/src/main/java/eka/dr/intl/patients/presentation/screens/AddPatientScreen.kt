package eka.dr.intl.patients.presentation.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.i18n.phonenumbers.PhoneNumberUtil
import eka.dr.intl.common.IAmCommon
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.utility.ProfileHelper.Companion.generateUniqueOid
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.icons.R
import eka.dr.intl.patients.data.local.convertors.Converters
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.data.remote.dto.response.GetFormFieldsResponse
import eka.dr.intl.patients.data.remote.dto.response.PatientDirectoryResponse
import eka.dr.intl.patients.presentation.components.AddPatientScreenContent
import eka.dr.intl.patients.presentation.viewModels.AddPatientViewModel
import eka.dr.intl.patients.utils.Conversions
import eka.dr.intl.patients.utils.DynamicFormKeys
import eka.dr.intl.ui.molecule.ButtonWrapper
import eka.dr.intl.ui.molecule.IconButtonWrapper
import eka.dr.intl.ui.organism.AppBar
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPatient(
    goBack: () -> Unit,
    context: Context
) {
    val businessId = OrbiUserManager.getSelectedBusiness() ?: ""
    val formFields = Gson().fromJson(
        (context.applicationContext as IAmCommon).getValue("$businessId-formFields", "[]"),
        Array<GetFormFieldsResponse.Form.AddPatientEntity>::class.java
    ).toList()
    val isPhoneCountryCodeEnabled =
        formFields.find { it.key == DynamicFormKeys.PHONE_COUNTRY_CODE.type }?.key?.isEmpty() == false
    val addPatientViewModel: AddPatientViewModel = koinViewModel()
    val name = addPatientViewModel.patientName.collectAsState().value
    val phone = addPatientViewModel.mobileNumber.collectAsState().value
    val gender = addPatientViewModel.gender.collectAsState().value
    val dob = addPatientViewModel.dob.collectAsState().value
    val age = addPatientViewModel.age.collectAsState().value
    val dateOfBirth = if (dob.isNotEmpty()) {
        dob.split("/").reversed().joinToString("-")
    } else {
        val ageToDob = convertAgeToDob(age)
        ageToDob.split("/").reversed().joinToString("-")
    }
    val salutation = addPatientViewModel.salutation.collectAsState().value
    val city = addPatientViewModel.city.collectAsState().value
    val referredByDoctorContact = addPatientViewModel.referredByDoctorContact.collectAsState().value
    val address = addPatientViewModel.address.collectAsState().value
    val pincode = addPatientViewModel.pincode.collectAsState().value
    val bloodGroup = addPatientViewModel.bloodGroup.collectAsState().value
    val emailId = addPatientViewModel.emailId.collectAsState().value
    val alternativePhone = addPatientViewModel.alternateContact.collectAsState().value
    val referredByDoctor = addPatientViewModel.referredByDoctor.collectAsState().value
    val maritalStatus = addPatientViewModel.maritalStatus.collectAsState().value
    val nameOfInformant = addPatientViewModel.nameOfInformant.collectAsState().value
    val channel = addPatientViewModel.channel.collectAsState().value
    val patientOccupation = addPatientViewModel.patientOccupation.collectAsState().value
    val guardianName = addPatientViewModel.guardiansName.collectAsState().value
    val relationshipWithPatient = addPatientViewModel.relationshipWithPatient.collectAsState().value
    val phoneCountryCode = addPatientViewModel.code.collectAsState().value
    val uhid = addPatientViewModel.uhid.collectAsState().value
    val patientOid = addPatientViewModel.patientOid.collectAsState().value
    val onApp = addPatientViewModel.onApp.collectAsState().value
    val uuid = addPatientViewModel.uuid.collectAsState().value
    val referredBy = addPatientViewModel.referredBy.collectAsState().value
    val referId = addPatientViewModel.referId.collectAsState().value
    val followup = addPatientViewModel.followUp.collectAsState().value
    val lastVisit = addPatientViewModel.lastVisit.collectAsState().value
    // check if name is filled
    val isNameFilled = name.isNotEmpty()
    // check if mobile number is filled
    val isMobileNumberFilled = phone.isNotEmpty()
    // check if gender is filled
    val isGenderFilled = gender.isNotEmpty()

    val numberValid = checkIfValidPhoneNumber(phCode = phoneCountryCode, phoneNumber = phone.trim())
    val isAgeFilled = dateOfBirth.isNotEmpty()
    val isAddPatientEnabled =
        isNameFilled && isGenderFilled && (if (isMobileNumberFilled) numberValid else true) && isAgeFilled

    val addPatientUiState = addPatientViewModel.addPatientToDirectory.collectAsState().value

    fun handleAddPatientToDirectory() {
        val formData: MutableList<PatientDirectoryResponse.Patient.FormData> = mutableListOf()
        for (key in DynamicFormKeys.entries) {
            val value: PatientDirectoryResponse.Patient.FormData = when (key) {
                DynamicFormKeys.REFERRED_DOCTOR_NUMBER -> PatientDirectoryResponse.Patient.FormData(
                    label = getLabelByKey(key),
                    key = key.type,
                    type = "number",
                    value = referredByDoctorContact
                )

                DynamicFormKeys.SALUTATION -> PatientDirectoryResponse.Patient.FormData(
                    label = getLabelByKey(key),
                    key = key.type,
                    type = "string",
                    value = salutation
                )

                DynamicFormKeys.PATIENT_ADDRESS -> PatientDirectoryResponse.Patient.FormData(
                    label = getLabelByKey(key),
                    key = key.type,
                    type = "string",
                    value = address
                )

                DynamicFormKeys.CITY -> PatientDirectoryResponse.Patient.FormData(
                    label = getLabelByKey(key),
                    key = key.type,
                    type = "string",
                    value = city
                )

                DynamicFormKeys.PINCODE -> PatientDirectoryResponse.Patient.FormData(
                    label = getLabelByKey(key),
                    key = key.type,
                    type = "string",
                    value = pincode
                )

                DynamicFormKeys.EMAIL -> PatientDirectoryResponse.Patient.FormData(
                    label = getLabelByKey(key),
                    key = key.type,
                    type = "string",
                    value = emailId
                )

                DynamicFormKeys.BLOOD_GROUP -> PatientDirectoryResponse.Patient.FormData(
                    label = getLabelByKey(key),
                    key = key.type,
                    type = "string",
                    value = bloodGroup
                )

                DynamicFormKeys.ALTERNATIVE_PHONE -> PatientDirectoryResponse.Patient.FormData(
                    label = getLabelByKey(key),
                    key = key.type,
                    type = "string",
                    value = alternativePhone
                )

                DynamicFormKeys.REFERRED_BY_DOCTOR -> PatientDirectoryResponse.Patient.FormData(
                    label = getLabelByKey(key),
                    key = key.type,
                    type = "string",
                    value = referredByDoctor
                )

                DynamicFormKeys.MARITAL_STATUS -> PatientDirectoryResponse.Patient.FormData(
                    label = getLabelByKey(key),
                    key = key.type,
                    type = "string",
                    value = maritalStatus
                )

                DynamicFormKeys.NAME_OF_INFORMANT -> PatientDirectoryResponse.Patient.FormData(
                    label = getLabelByKey(key),
                    key = key.type,
                    type = "string",
                    value = nameOfInformant
                )

                DynamicFormKeys.CHANNEL -> PatientDirectoryResponse.Patient.FormData(
                    label = getLabelByKey(key),
                    key = key.type,
                    type = "string",
                    value = channel
                )

                DynamicFormKeys.PATIENT_OCCUPATION -> PatientDirectoryResponse.Patient.FormData(
                    label = getLabelByKey(key),
                    key = key.type,
                    type = "string",
                    value = patientOccupation
                )

                DynamicFormKeys.GUARDIAN_NAME -> PatientDirectoryResponse.Patient.FormData(
                    label = getLabelByKey(key),
                    key = key.type,
                    type = "string",
                    value = guardianName
                )

                DynamicFormKeys.RELATIONSHIP_WITH_PATIENT -> PatientDirectoryResponse.Patient.FormData(
                    label = getLabelByKey(key),
                    key = key.type,
                    type = "string",
                    value = relationshipWithPatient
                )

                DynamicFormKeys.PHONE_COUNTRY_CODE -> PatientDirectoryResponse.Patient.FormData(
                    label = getLabelByKey(key),
                    key = key.type,
                    type = "string",
                    value = phoneCountryCode
                )
            }
            formData.add(value)
        }
        if (uhid.isNotEmpty()) {
            formData.add(
                PatientDirectoryResponse.Patient.FormData(
                    label = "UHID",
                    key = "uhid",
                    type = "string",
                    value = uhid
                )
            )
        }
        val gson = Gson()

        // check if phonecode contains a preceding + sign if not put it in
        if (!phoneCountryCode.startsWith("+") && isPhoneCountryCodeEnabled) {
            Toast.makeText(context, "Invalid country code", Toast.LENGTH_SHORT).show()
            return
        }
        // removes non-numeric characters with the regex
        val regex = Regex("[^0-9]")
        val countryCode = phoneCountryCode
            .takeIf { it.isNotEmpty() }
            ?.let { regex.replace(it, "").toIntOrNull() }

        val genderEnum = Converters().toGenderFromString(gender)
        val businessId = OrbiUserManager.getSelectedBusiness() ?: ""

        val data = PatientEntity(
            oid = if (!patientOid.isNullOrEmpty()) patientOid else generateUniqueOid(),
            uuid = uuid.ifEmpty { null },
            name = name,
            email = emailId,
            uhid = uhid,
            countryCode = countryCode,
            phone = phone.trim().takeIf { it.isNotEmpty() }?.toLongOrNull(),
            gender = genderEnum,
            age = Conversions.formYYYYMMDDToLong(dateOfBirth),
            onApp = onApp,
            bloodGroup = Converters().toBloodGroupFromString(bloodGroup),
            formData = gson.toJson(
                formData,
                object : TypeToken<List<PatientDirectoryResponse.Patient.FormData?>>() {}.type
            ),
            archived = false,
            createdAt = Date().time,
            updatedAt = Date().time,
            referredBy = referredBy,
            referId = referId,
            followUp = Conversions.fromTimestampToLong(followup),
            lastVisit = Conversions.fromTimestampToLong(lastVisit),
            businessId = businessId,
            newPatient = patientOid.isNullOrEmpty(),
            dirty = true
        )
        addPatientViewModel.addPatientToDirectory(data)
    }

    val shouldGoBack by remember(addPatientUiState) {
        derivedStateOf {
            !addPatientUiState.loading &&
                    addPatientUiState.error.isNullOrEmpty() &&
                    addPatientUiState.data != null
        }
    }

    LaunchedEffect(shouldGoBack) {
        if (shouldGoBack) {
            goBack()
        }
    }


    addPatientUiState.error?.let {
        Toast.makeText(context, "Error Adding Patient", Toast.LENGTH_SHORT).show()
    }

    val title = if (uuid.isEmpty()) "Add Patient" else "Update Patient"

    Scaffold(
        modifier = Modifier
            .padding(bottom = 40.dp)
            .fillMaxSize(),
        topBar = {
            AppBar(
                title = title,
                navigationIcon = {
                    IconButtonWrapper(
                        onClick = { goBack() },
                        icon = R.drawable.ic_arrow_left_regular,
                        contentDescription = "",
                        iconSize = 16.dp
                    )
                },
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarwinTouchNeutral0)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ButtonWrapper(
                        enabled = isAddPatientEnabled && !addPatientUiState.loading,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { handleAddPatientToDirectory() },
                        text = title
                    )
                }
            }
        }
    ) { innerPadding ->
        AddPatientScreenContent(innerPadding)
    }
}

fun getLabelByKey(key: DynamicFormKeys): String {
    return when (key) {
        DynamicFormKeys.SALUTATION -> "Salutation"
        DynamicFormKeys.PATIENT_ADDRESS -> "Patient Address"
        DynamicFormKeys.CITY -> "City"
        DynamicFormKeys.PINCODE -> "Pincode"
        DynamicFormKeys.EMAIL -> "EMail ID"
        DynamicFormKeys.BLOOD_GROUP -> "Blood Group"
        DynamicFormKeys.ALTERNATIVE_PHONE -> "Alternative Phone"
        DynamicFormKeys.REFERRED_BY_DOCTOR -> "Referred By Doctor"
        DynamicFormKeys.MARITAL_STATUS -> "Marital Status"
        DynamicFormKeys.NAME_OF_INFORMANT -> "Name of Informant"
        DynamicFormKeys.CHANNEL -> "Channel"
        DynamicFormKeys.PATIENT_OCCUPATION -> "Patient's Occupation"
        DynamicFormKeys.GUARDIAN_NAME -> "Guardian Name"
        DynamicFormKeys.RELATIONSHIP_WITH_PATIENT -> "Relationship with Patient"
        DynamicFormKeys.PHONE_COUNTRY_CODE -> "Phone Country Code"
        DynamicFormKeys.REFERRED_DOCTOR_NUMBER -> "Referred Doctor's Number"
    }
}

fun checkIfValidPhoneNumber(
    phCode: String? = "+91",
    phoneNumber: String?,
    countryCodeEnum: String? = null
): Boolean {
    if (phoneNumber.isNullOrBlank()) {
        return false
    }
    // Using Google's libphonenumber to validate international phone numbers
    return try {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val parsedNumber = phoneNumberUtil.parse((phCode + phoneNumber).trim(), countryCodeEnum)
        phoneNumberUtil.isValidNumber(parsedNumber)
    } catch (e: Exception) {
        false
    }
}

fun convertAgeToDob(age: Long?): String {
    return if (age != null) {
        val date = Date(age)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.format(date)
    } else {
        ""
    }
}