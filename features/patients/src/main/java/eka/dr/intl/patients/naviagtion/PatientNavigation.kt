package eka.dr.intl.patients.naviagtion

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import kotlinx.serialization.Serializable

@Serializable
data class PatientNavModel(
    val pid: String? = null,
    val from: String? = null,
    val search: Boolean = false
)

fun NavController.navigateToPatientDirectory(
    pid: String? = null,
    from: String? = null,
    search: Boolean = false,
    navOptions: NavOptions? = null
) {
    navigate(PatientNavModel(pid, from, search), navOptions)
}

@Serializable
data class PatientActionsScreenNavModel(
    val pid: String,
)

fun NavController.navigateToPatientActionsScreen(
    pid: String,
    navOptions: NavOptions? = null,
) {
    navigate(PatientActionsScreenNavModel(pid = pid), navOptions = navOptions)
}


@Serializable
data class MedicalRecordsNavModel(
    val filterId: String,
    val ownerId: String,
    val name: String? = null,
    val age: Int? = null,
    val gen: String? = null,
    val links: String? = null,
    val success: String? = null,
    val isPDFUpload: Boolean = true
)

fun NavController.navigateToMedicalRecords(
    filterId: String,
    ownerId: String,
    name: String? = null,
    age: Int? = null,
    gen: String? = null,
    links: String? = null,
    success: String? = null,
    isPDFUpload: Boolean = true,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(
        MedicalRecordsNavModel(
            filterId = filterId,
            ownerId = ownerId,
            name = name,
            age = age,
            gen = gen,
            links = links,
            success = success,
            isPDFUpload = isPDFUpload
        )
    ) {
        navOptions()
    }
}

@Serializable
data class AddPatientToDirectoryNavModel(
    val phone: String? = null,
    val email: String? = null,
    val name: String? = null,
    val pid: String? = null,
    val from: String? = null,
    val isAbha: Boolean = false
)

fun NavController.navigateToAddPatientToDirectory(
    phone: String? = null,
    email: String? = null,
    name: String? = null,
    pid: String? = null,
    from: String? = null,
    isAbha: Boolean = false,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(AddPatientToDirectoryNavModel(phone, email, name, pid, from, isAbha)) {
        navOptions()
    }
}