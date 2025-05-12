package eka.dr.intl.patients.presentation.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.Urls
import eka.dr.intl.common.utility.NavigateToAddPatient
import eka.dr.intl.common.utility.NavigateToMedicalRecords
import eka.dr.intl.common.utility.NavigateToPatientActionScreen
import eka.dr.intl.common.utility.ProfileHelper
import eka.dr.intl.ekatheme.color.DarwinTouchRed
import eka.dr.intl.icons.R
import eka.dr.intl.patients.naviagtion.PatientDirectoryNavigation
import eka.dr.intl.patients.presentation.components.PatientDirectoryScreenContent
import eka.dr.intl.patients.presentation.viewModels.PatientCount
import eka.dr.intl.patients.presentation.viewModels.PatientDirectoryViewModel
import eka.dr.intl.typography.touchCalloutBold
import eka.dr.intl.ui.atom.IconWrapper
import eka.dr.intl.ui.custom.ProfileImage
import eka.dr.intl.ui.custom.ProfileImageProps
import eka.dr.intl.ui.molecule.IconButtonWrapper
import eka.dr.intl.ui.organism.AppBar
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDirectoryScreen(
    context: Context,
    patientOid: String? = null,
    isAbha: Boolean? = false,
    drawerState: DrawerState,
    onBackClick: () -> Unit,
    openMedicalRecords: NavigateToMedicalRecords,
    openAddPatientNavigation: NavigateToAddPatient,
    openPatientActionsScreen: NavigateToPatientActionScreen,
) {
    val viewModel: PatientDirectoryViewModel = koinViewModel()
    val scope = rememberCoroutineScope()
    val navigation = PatientDirectoryNavigation(
        openMedicalRecords = openMedicalRecords,
        openAddPatientNavigation = openAddPatientNavigation,
        openPatientActionsScreen = openPatientActionsScreen
    )
    val name = OrbiUserManager.getUserTokenData()?.name
    val loggedInUser = OrbiUserManager.getUserTokenData()?.oid
    val docProfilePic = Urls.DOC_PROFILE_URL + loggedInUser
    val count = viewModel.count.collectAsState(PatientCount())
    val isPatientCountZero = count.value.allPatientCount == 0
    Scaffold(
        topBar = {
            AppBar(
                borderColor = Color.Transparent,
                title = "Patients ${if (!isPatientCountZero) "(${count.value.allPatientCount})" else ""}",
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(48.dp)
                            .clickable(
                                onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                },
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple()
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        ProfileImage(
                            ProfileImageProps(
                                oid = loggedInUser?.toLongOrNull(),
                                url = docProfilePic,
                                initials = ProfileHelper.getInitials(name),
                            )
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (count.value.patientNotSyncedCount > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconWrapper(
                                    icon = R.drawable.ic_triangle_exclamation_solid,
                                    contentDescription = "Patient Not Synced",
                                    tint = DarwinTouchRed,
                                    modifier = Modifier
                                        .size(16.dp)
                                )
                                Text(
                                    text = "${count.value.patientNotSyncedCount}",
                                    style = touchCalloutBold,
                                    color = DarwinTouchRed,
                                )
                            }
                        }
                        IconButtonWrapper(
                            icon = R.drawable.ic_arrows_rotate_regular,
                            onClick = {
                                viewModel.refreshPatients(context.applicationContext)
                            },
                            contentDescription = "Refresh",
                            iconSize = 16.dp
                        )
                    }
                }
            )
        },
        content = {
            PatientDirectoryScreenContent(
                context = context,
                paddingValues = it,
                patientOid = patientOid,
                isAbha = isAbha,
                navigation = navigation,
                onBackClick = onBackClick,
            )
        }
    )
}