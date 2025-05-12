package eka.dr.intl.assistant.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eka.dr.intl.icons.R
import eka.dr.intl.assistant.navigation.ChatBotPatientSessionNavModel
import eka.dr.intl.assistant.presentation.components.EkaChatBotTopBar
import eka.dr.intl.assistant.presentation.components.EkaChatPatientsRow
import eka.dr.intl.assistant.presentation.components.EkaChatPatientsRowData
import eka.dr.intl.assistant.presentation.models.ChatSession
import eka.dr.intl.assistant.presentation.viewmodel.EkaChatViewModel
import eka.dr.intl.assistant.utility.ChatUtils
import eka.dr.intl.assistant.utility.MessageType
import eka.dr.intl.common.data.dto.response.ChatContext
import eka.dr.intl.ekatheme.color.DarwinTouchGreen
import eka.dr.intl.ekatheme.color.DarwinTouchGreenBgLight
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral100
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral50
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral600
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgLight
import eka.dr.intl.ekatheme.color.DarwinTouchYellowDark
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.typography.touchCalloutBold
import eka.dr.intl.typography.touchLabelRegular

enum class DetailsScreenMode(val value: String) {
    PATIENT_DETAILS("PATIENT_DETAILS"),
    PATIENT_BOTTOM_SHEET("PATIENT_BOTTOM_SHEET"),
    CLINICAL_NOTES("CLINICAL_NOTES");

    companion object {
        fun fromValue(value: String): DetailsScreenMode {
            return values().find { it.value == value } ?: PATIENT_DETAILS
        }
    }
}

data class MessageEntity2(
    val headline: String,
    val subHeadline: String,
    val icon: Int,
    val time: String
)

@Composable
fun EkaBotPatientDetailScreen(
    navData: ChatBotPatientSessionNavModel,
    chatViewModel: EkaChatViewModel,
    onClick: (String, ChatContext?) -> Unit,
    onBackClick: () -> Unit,
    onEmptyScreen: () -> Unit,
) {
    val mode = remember {
        DetailsScreenMode.fromValue(navData.mode ?: "PATIENT_DETAILS")
    }
    val sessions by chatViewModel.groupedSessionsByContext.collectAsState()
    val isSessionLoading by chatViewModel.isSessionLoading.collectAsState()
    var patientId by remember {
        mutableStateOf(navData.patientId)
    }
    var patientEntry: Map.Entry<ChatContext, List<ChatSession>>? by remember {
        mutableStateOf(null)
    }
    var patient: PatientEntity? by remember {
        mutableStateOf(null)
    }
    LaunchedEffect(Unit) {
        chatViewModel.getChatSessions(null)
    }

    LaunchedEffect(sessions) {
        patientEntry = sessions.entries.find { it.key.patientId == patientId }
        if (patientEntry?.value.isNullOrEmpty() && !isSessionLoading) {
            onEmptyScreen.invoke()
        }
    }

    val titleText = when (mode) {
        DetailsScreenMode.PATIENT_DETAILS -> patientEntry?.key?.patientName ?: "Patient"
        DetailsScreenMode.PATIENT_BOTTOM_SHEET -> patientEntry?.key?.patientName ?: "Patient"
        DetailsScreenMode.CLINICAL_NOTES -> "Diet chart for low vitamins"
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = DarwinTouchNeutral100,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarwinTouchNeutral0),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                EkaChatBotTopBar(
                    consultationStarted = true,
                    hasClinicalNotes = false,
                    title = titleText,
                    subTitle = if (mode == DetailsScreenMode.CLINICAL_NOTES) patientEntry?.key?.patientName
                        ?: "Patient" else null,
                    onClick = {
                        onBackClick.invoke()
                    }
                )
                if (mode == DetailsScreenMode.PATIENT_DETAILS) ChatsFoundComponent(
                    chatNo = patientEntry?.value?.size ?: 0
                )
            }
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
            ) {
                when (mode) {
                    DetailsScreenMode.PATIENT_BOTTOM_SHEET,
                    DetailsScreenMode.PATIENT_DETAILS -> {
                        val patientList = patientEntry?.value?.toList() ?: emptyList()
                        itemsIndexed(patientList) { index, message ->
                            var headline = when (message.message.msgType) {
                                MessageType.TEXT.stringValue -> message.message.messageText
                                else -> "Conversation"
                            }
                            if (headline.isNullOrEmpty()) {
                                headline = "Conversation"
                            }
                            EkaChatPatientsRow(
                                data = EkaChatPatientsRowData(
                                    headlineText = headline,
                                    time = ChatUtils.getTimeStampString(message.message.createdAt),
                                    subHeadline = chatViewModel.getSubHeadline(message),
                                    draftNumber = message.totalConversations,
                                    draftNumberColor = DarwinTouchYellowDark,
                                    spaceBetweenSubHeadlineAndTime = true,
                                    icon = R.drawable.ic_messages_regular,
                                    iconSize = 20,
                                    backgroundColor = Color.White
                                ),
                                onClick = {
                                    onClick.invoke(
                                        message.message.sessionId,
                                        patientEntry?.key
                                    )
                                }
                            )
                            if (index < patientList.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(DarwinTouchNeutral600)
                                )
                            }
                        }
                    }

                    DetailsScreenMode.CLINICAL_NOTES -> {
                        val clinicalList = List(5) {
                            MessageEntity2(
                                headline = "Clinical notes",
                                subHeadline = "08 Jan’24",
                                time = "2 m ago",
                                icon = R.drawable.ic_file_waveform_solid
                            )
                        }
                        val patientList = patientEntry?.value?.toList()
                            ?.filter { (it.message.msgType == MessageType.VOICE_2_RX_PRESCRIPTION_DRAFT.stringValue) || (it.message.msgType == MessageType.VOICE_2_RX_PRESCRIPTION_COMPLETED.stringValue) }
                            ?: emptyList()
                        itemsIndexed(patientList) { index, clinicalNote ->
                            EkaChatPatientsRow(
                                data = EkaChatPatientsRowData(
                                    headlineText = "Clinical notes",
                                    subHeadline = ChatUtils.getTimeStampString(clinicalNote.message.createdAt),
                                    time = "08 Jan’24",
                                    spaceBetweenSubHeadlineAndTime = true,
                                    icon = R.drawable.ic_file_waveform_solid,
                                    iconSize = 16,
                                    iconColor = DarwinTouchGreen,
                                    backgroundColor = DarwinTouchGreenBgLight
                                ),
                                onClick = {}
                            )
                            if (index < clinicalList.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(DarwinTouchNeutral600)
                                )
                            }
                        }
                    }

                    else -> { /* Handle other cases if needed */
                    }
                }
            }
        },
        floatingActionButton = {
            if (mode == DetailsScreenMode.PATIENT_DETAILS) {
                FloatingActionButton(
                    onClick = {
                        onClick.invoke(ChatUtils.getNewSessionId(), patientEntry?.key)
                    },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = DarwinTouchPrimaryBgLight
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            modifier = Modifier.size(16.dp),
                            painter = painterResource(id = R.drawable.ic_pen_solid),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(DarwinTouchNeutral1000)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "New",
                            style = touchCalloutBold,
                            color = DarwinTouchNeutral1000
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ChatsFoundComponent(chatNo: Int) {
    var label = "$chatNo "
    label += if (chatNo == 1) {
        "chat found"
    } else {
        "chats found"
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarwinTouchNeutral50)
    ) {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarwinTouchNeutral600)
        )
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            text = label,
            style = touchLabelRegular,
            color = DarwinTouchNeutral600
        )
    }
}