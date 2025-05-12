package eka.dr.intl.patients.presentation.components

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eka.dr.intl.common.IAmCommon
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.PageIdentifier
import eka.dr.intl.common.Restrictions
import eka.dr.intl.common.utility.DateUtils
import eka.dr.intl.common.utility.NavigateToAddPatient
import eka.dr.intl.common.utility.NavigateToMedicalRecords
import eka.dr.intl.common.utility.ProfileHelper
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral50
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral600
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgLight
import eka.dr.intl.ekatheme.color.DarwinTouchRed
import eka.dr.intl.ekatheme.color.DarwinTouchRedBgLight
import eka.dr.intl.icons.R
import eka.dr.intl.patients.data.local.convertors.Converters
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.naviagtion.PatientActionsScreenNavModel
import eka.dr.intl.patients.naviagtion.PatientDirectoryNavigation
import eka.dr.intl.patients.presentation.viewModels.PatientDirectoryViewModel
import eka.dr.intl.patients.utils.PatientDirectoryUtils.Companion.getProfileImageByOid
import eka.dr.intl.typography.touchBodyRegular
import eka.dr.intl.typography.touchCalloutRegular
import eka.dr.intl.typography.touchFootnoteBold
import eka.dr.intl.typography.touchFootnoteRegular
import eka.dr.intl.typography.touchSubheadlineRegular
import eka.dr.intl.typography.touchTitle2Regular
import eka.dr.intl.ui.atom.IconWrapper
import eka.dr.intl.ui.custom.ProfileImage
import eka.dr.intl.ui.custom.ProfileImageProps
import eka.dr.intl.ui.molecule.ButtonWrapper
import eka.dr.intl.ui.molecule.ButtonWrapperType
import eka.dr.intl.ui.molecule.IconButtonWrapper
import eka.dr.intl.ui.organism.AppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientActionsScreen(
    navData: PatientActionsScreenNavModel,
    onClose: () -> Unit,
    openMedicalRecords: NavigateToMedicalRecords,
    openAddPatientNavigation: NavigateToAddPatient,
    navigateToDocAssist : () -> Unit,
    navigateToVoice2Rx : (patientId : String) -> Unit
) {
    val context = LocalContext.current
    var selectedPatient by remember { mutableStateOf<PatientEntity?>(null) }
    val viewModel: PatientDirectoryViewModel = koinViewModel()
    LaunchedEffect(Unit) {
        viewModel.getAllDoctors()
    }
    LaunchedEffect(navData.pid) {
        selectedPatient = viewModel.getPatientByOid(patientOid = navData.pid)
    }

    if (selectedPatient == null) {
        return
    }
    val navigation = PatientDirectoryNavigation(
        openMedicalRecords = openMedicalRecords,
        openAddPatientNavigation = openAddPatientNavigation,
        openPatientActionsScreen = null
    )
    val isDoctor = OrbiUserManager.getUserRole() == "DOCTOR"
    val uhid = selectedPatient?.uhid
    val isPatientOnApp = selectedPatient?.onApp == true
    val profileImage = getProfileImageByOid(navData.pid)
    val initials = ProfileHelper.getInitials(
        name = selectedPatient?.name,
    )
    val name = selectedPatient?.name ?: ""
    val mobile =
        if (selectedPatient?.phone != null) "${selectedPatient?.countryCode} ${selectedPatient?.phone}" else null
    val doctorOid = if (isDoctor) {
        OrbiUserManager.getUserTokenData()?.oid
    } else {
        runBlocking {
            viewModel.getAllDoctors().firstOrNull()?.id
        }
    } ?: ""
    val gender = Converters().fromGenderToString(selectedPatient?.gender)?.replaceFirstChar {
        it.uppercase()
    }
    val showDialog = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val isPatientNumberAllowed =
        (context.applicationContext as IAmCommon).isAllowedToAccess(Restrictions.PATIENT_MOBILE_NUMBER)
    val isDocAssistAllowed =
        (context.applicationContext as IAmCommon).isAllowedToAccess(Restrictions.DOC_ASSIST)
    val isEditPatientAllowed =
        (context.applicationContext as IAmCommon).isAllowedToAccess(Restrictions.PATIENT_DATABASE)

    if (showDialog.value) {
        AlertDialog(
            containerColor = DarwinTouchNeutral0,
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                ButtonWrapper(
                    type = ButtonWrapperType.TEXT,
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            selectedPatient?.let {
                                viewModel.archivePatient(patient = it, onSuccess = onClose)
                                showDialog.value = false
                            }
                        }
                    },
                    text = "OK"
                )
            },
            dismissButton = {
                ButtonWrapper(
                    type = ButtonWrapperType.TEXT,
                    onClick = {
                        showDialog.value = false
                    },
                    text = "Cancel"
                )
            },
            title = {
                Text(
                    text = "Archive Patient",
                    style = touchTitle2Regular,
                    color = DarwinTouchNeutral1000
                )
            },
            text = {
                Text(
                    "Archiving $name will result in the loss of all Appointment History. Are you sure you want to proceed with archiving this patient?",
                    style = touchCalloutRegular,
                    color = DarwinTouchNeutral800
                )
            })
    }

    Scaffold(
        containerColor = DarwinTouchNeutral0,
        topBar = {
            AppBar(
                borderColor = Color.Transparent,
                actions = {
                    if (isEditPatientAllowed) {
                        IconButtonWrapper(
                            onClick = {
                                navigation.navigateToAddPatient(pid = navData.pid)
                            },
                            icon = R.drawable.ic_pen_to_square_regular,
                            contentDescription = "Edit Patient",
                            iconSize = 16.dp,
                        )
                    }
                },
                title = "",
                navigationIcon = {
                    IconButtonWrapper(
                        onClick = {
                            onClose()
                        },
                        icon = R.drawable.ic_arrow_left_regular,
                        contentDescription = "Back",
                        iconSize = 16.dp,
                    )
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .background(DarwinTouchNeutral0),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                ProfileImage(
                    ProfileImageProps(
                        oid = navData.pid.toLongOrNull(),
                        url = profileImage,
                        initials = initials,
                        size = 72.dp,
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = name,
                    style = touchTitle2Regular,
                    fontWeight = FontWeight.Bold,
                    color = DarwinTouchNeutral1000,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!gender.isNullOrEmpty()) {
                        Text(
                            text = gender,
                            style = touchFootnoteRegular,
                            color = DarwinTouchNeutral600
                        )
                    }
                    if (!mobile.isNullOrEmpty() && isPatientNumberAllowed) {
                        if (!gender.isNullOrEmpty()) {
                            IconWrapper(
                                icon = R.drawable.ic_circle_solid,
                                contentDescription = "Circle",
                                modifier = Modifier.size(3.dp),
                                boundingBoxSize = 3.dp
                            )
                        }
                        Text(
                            text = mobile,
                            style = touchCalloutRegular,
                            color = DarwinTouchNeutral600
                        )
                    }
                    if (!uhid.isNullOrEmpty()) {
                        if (!gender.isNullOrEmpty() || (!mobile.isNullOrEmpty() && isPatientNumberAllowed)) {
                            IconWrapper(
                                icon = R.drawable.ic_circle_solid,
                                contentDescription = "Circle",
                                modifier = Modifier.size(3.dp),
                                boundingBoxSize = 3.dp
                            )
                        }
                        Text(
                            text = uhid.toString(),
                            style = touchCalloutRegular,
                            color = DarwinTouchNeutral600
                        )
                    }
                }
                if (selectedPatient?.dirty == true) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .background(DarwinTouchRedBgLight, RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        IconWrapper(
                            icon = R.drawable.ic_triangle_exclamation_solid,
                            contentDescription = "Not Synced",
                            modifier = Modifier.size(16.dp),
                            tint = DarwinTouchRed
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "Patient details are not synced",
                            style = touchFootnoteBold,
                            color = DarwinTouchRed
                        )
                    }
                }
                if (isPatientOnApp) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarwinTouchPrimaryBgLight, RoundedCornerShape(8.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .clip(
                                    RoundedCornerShape(8.dp)
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            IconWrapper(
                                icon = R.drawable.ic_eka_logo_custom,
                                contentDescription = "Eka Logo",
                                modifier = Modifier.size(16.dp),
                                tint = DarwinTouchPrimary
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = "Patient is connected on Eka Care mobile app",
                                style = touchFootnoteBold,
                                color = DarwinTouchPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LongChipCta(
                    icon = R.drawable.ic_file_waveform_regular,
                    iconColor = Color.Unspecified,
                    cta = "Medical Records",
                    onClick = {
                        scope.launch {
                            val age = selectedPatient?.age?.let { DateUtils.calculateAge(it) }
                            val params = JSONObject()
                            params.put("isNewUser", selectedPatient?.uuid.isNullOrEmpty())
                            if (selectedPatient?.uuid.isNullOrEmpty()) {
                                Toast.makeText(
                                    context, "Patient is not synced", Toast.LENGTH_SHORT
                                ).show()
                                return@launch
                            }
                            navigation.navigateToMedicalRecord(
                                patientOid = navData.pid,
                                doctorId = doctorOid,
                                links = selectedPatient?.links?.joinToString(","),
                                name = selectedPatient?.name ?: "",
                                age = age,
                                gender = Converters().fromGenderToString(selectedPatient?.gender)
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (isDocAssistAllowed) {
                    LongChipCta(
                        icon = R.drawable.ic_ai_chat_custom,
                        iconColor = Color.Unspecified,
                        cta = "DocAssist AI",
                        onClick = {
                            navigateToDocAssist()
                        }) {
                        Text(
                            text = "Ask anything",
                            style = touchSubheadlineRegular,
                            color = DarwinTouchNeutral800
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LongChipCta(
                        icon = R.drawable.ic_voice_2_rx_custom,
                        iconColor = Color.Unspecified,
                        cta = "Start Voice-to-Rx",
                        onClick = {
                            navigateToVoice2Rx(navData.pid)
                        }) {
                        Text(
                            text = "Ask anything",
                            style = touchSubheadlineRegular,
                            color = DarwinTouchNeutral800
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                ListItem(
                    colors = ListItemDefaults.colors(
                        containerColor = DarwinTouchNeutral0
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .border(width = 1.dp, color = DarwinTouchNeutral50, shape = RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true)
                        ) {
                            showDialog.value = true
                        },
                    leadingContent = {
                        IconWrapper(
                            icon = R.drawable.ic_trash_regular,
                            contentDescription = "Action Icon",
                            tint = DarwinTouchRed,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    headlineContent = {
                        Text(
                            text = "Archive Patient",
                            modifier = Modifier,
                            style = touchBodyRegular,
                            color = DarwinTouchRed
                        )
                    },
                    trailingContent = {
                        IconWrapper(
                            icon = R.drawable.ic_chevron_right_regular,
                            contentDescription = "Action Icon",
                            tint = DarwinTouchRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
    }
}