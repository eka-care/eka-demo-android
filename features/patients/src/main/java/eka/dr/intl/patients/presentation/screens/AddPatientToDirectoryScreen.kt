package eka.dr.intl.patients.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import eka.dr.intl.patients.naviagtion.AddPatientToDirectoryNavModel
import eka.dr.intl.patients.presentation.components.handleAddPatient
import eka.dr.intl.patients.presentation.viewModels.AddPatientViewModel
import eka.dr.intl.patients.presentation.viewModels.PatientDirectoryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddPatientToDirectoryScreen(
    navData: AddPatientToDirectoryNavModel,
    navigateToPatientDirectory: () -> Unit,
) {
    val context = LocalContext.current
    val name = navData.name
    val phone = navData.phone
    val email = navData.email
    val pid = navData.pid
    val from = navData.from

    val viewModel: AddPatientViewModel = koinViewModel()
    val ptViewModel: PatientDirectoryViewModel = koinViewModel()

    LaunchedEffect(phone, email, name) {
        if (!phone.isNullOrEmpty()) {
            viewModel.updateMobileNumber(phone)
        } else if (!email.isNullOrEmpty()) {
            viewModel.updateEmailId(email)
        } else if (!name.isNullOrEmpty()) {
            viewModel.updatePatientName(name)
        }
    }
    LaunchedEffect(from, pid) {
        if (!from.isNullOrEmpty()) {
            ptViewModel.setFrom(from)
        }
        if (!pid.isNullOrEmpty()) {
            val formFieldsData = ptViewModel.getPatientByOid(pid)
            handleAddPatient(formFieldsData, viewModel)
        }
    }

    AddPatient(
        goBack = navigateToPatientDirectory,
        context = context
    )
}