package eka.dr.intl.patients.presentation.screens

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import eka.dr.intl.common.IAmCommon
import eka.dr.intl.common.Restrictions
import eka.dr.intl.common.utility.NavigateToAddPatient
import eka.dr.intl.common.utility.NavigateToMedicalRecords
import eka.dr.intl.common.utility.NavigateToPatientActionScreen
import eka.dr.intl.patients.naviagtion.PatientNavModel
import eka.dr.intl.patients.presentation.viewModels.PatientDirectoryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PatientScreen (
    navData: PatientNavModel,
    drawerState : DrawerState,
    onBackClick: () -> Unit,
    openMedicalRecords: NavigateToMedicalRecords,
    openAddPatientNavigation: NavigateToAddPatient,
    openPatientActionsScreen: NavigateToPatientActionScreen,
) {
    val context = LocalContext.current
    val viewModel = koinViewModel<PatientDirectoryViewModel>()

    LaunchedEffect(navData, context) {
        val search = navData.search
        val from = navData.from
        val isAllPatientsAllowed = (context.applicationContext as IAmCommon)
            .isAllowedToAccess(Restrictions.ALL_PT)

        if (search) {
            viewModel.setActive(true)
        }
        if (!from.isNullOrEmpty()) {
            viewModel.setFrom(from)
        }
        if (!isAllPatientsAllowed) {
            viewModel.setActive(true)
            viewModel.setFrom("homepage")
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getAllDoctors()
        viewModel.refreshPatientDirectory()
    }
    PatientDirectoryScreen(
        context = context,
        drawerState = drawerState,
        patientOid = navData.pid,
        openMedicalRecords = openMedicalRecords,
        onBackClick = onBackClick,
        openAddPatientNavigation = openAddPatientNavigation,
        openPatientActionsScreen = openPatientActionsScreen
    )
}