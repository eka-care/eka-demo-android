package eka.dr.intl.patients.naviagtion

import eka.dr.intl.common.utility.NavigateToAddPatient
import eka.dr.intl.common.utility.NavigateToMedicalRecords
import eka.dr.intl.common.utility.NavigateToPatientActionScreen
import org.json.JSONObject

class PatientDirectoryNavigation(
    val openMedicalRecords: NavigateToMedicalRecords,
    val openAddPatientNavigation: NavigateToAddPatient,
    val openPatientActionsScreen: NavigateToPatientActionScreen,
) {

    fun navigateToPatientActionsScreen(
        pid: String
    ) {
        openPatientActionsScreen?.invoke(pid)
    }

    fun navigateToAddPatientWithInputText(
        inputText: String,
        from: String? = "",
    ) {
        val mobile = inputText.replace("[^0-9]".toRegex(), "")
        val isMobile = mobile.length == 10
        val isEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(inputText).matches()

        navigateToAddPatient(
            phone = if (isMobile) mobile else "",
            email = if (isEmail) inputText else "",
            name = if (isEmail || isMobile) "" else inputText,
            from = from,
        )
    }

    fun navigateToAddPatient(
        pid: String? = "",
        from: String? = "",
        phone: String? = "",
        email: String? = "",
        name: String? = "",
    ) {
        val params = JSONObject()
        params.put("pid", pid)
        params.put("from", from)
        params.put("phone", phone)
        params.put("email", email)
        params.put("name", name)
        openAddPatientNavigation(
            pid,
            from,
            phone,
            email,
            name
        )
    }


    fun navigateToMedicalRecord(
        patientOid: String,
        name: String?,
        gender: String?,
        doctorId: String,
        age: Int?,
        links: String?,
    ) {
        openMedicalRecords(
            patientOid,
            doctorId,
            links,
            name,
            gender,
            age
        )
    }
}