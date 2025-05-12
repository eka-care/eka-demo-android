package eka.dr.intl.assistant.presentation.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eka.conversation.common.PermissionUtils
import com.eka.conversation.common.Utils
import com.eka.voice2rx_sdk.common.Voice2RxUtils
import com.eka.voice2rx_sdk.data.local.models.Voice2RxType
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import eka.care.documents.ui.presentation.model.RecordModel
import eka.dr.intl.assistant.navigation.ChatScreenNavModel
import eka.dr.intl.assistant.presentation.components.BottomBarMainScreen
import eka.dr.intl.assistant.presentation.components.EkaChatBotTopBar
import eka.dr.intl.assistant.presentation.components.MicrophonePermissionAlertDialog
import eka.dr.intl.assistant.presentation.components.Voice2RxInitialBottomSheet
import eka.dr.intl.assistant.presentation.viewmodel.EkaChatViewModel
import eka.dr.intl.assistant.presentation.viewmodel.Voice2RxViewModel
import eka.dr.intl.assistant.utility.ActionType
import eka.dr.intl.assistant.utility.ChatUtils
import eka.dr.intl.assistant.utility.EkaChatSheetContent
import eka.dr.intl.assistant.utility.Extension.capitalizeFirstWordOfEachString
import eka.dr.intl.assistant.utility.MessageType
import eka.dr.intl.assistant.utility.Voice2RxSessionManager
import eka.dr.intl.common.IAmCommon
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.R
import eka.dr.intl.common.data.dto.response.CTA
import eka.dr.intl.common.data.dto.response.ChatContext
import eka.dr.intl.common.data.dto.response.ConsultationData
import eka.dr.intl.common.presentation.component.ConsultationState
import eka.dr.intl.common.presentation.state.DocStatus
import eka.dr.intl.common.presentation.viewmodel.DoctorStatusViewModel
import eka.dr.intl.common.utility.NavigateToTranscriptScreen
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral600
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.presentation.viewModels.PatientDirectoryViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EkaChatBotMainScreen(
    navData: ChatScreenNavModel,
    viewModel: EkaChatViewModel,
    voice2RxViewModel: Voice2RxViewModel,
    doctorStatusViewModel: DoctorStatusViewModel,
    onBackClick: () -> Unit,
    openTranscriptScreen: NavigateToTranscriptScreen
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val patientViewModel: PatientDirectoryViewModel = koinViewModel<PatientDirectoryViewModel>()
    var isInputBottomSheetVisible by remember { mutableStateOf(true) }
    val sessionMessages by viewModel.sessionMessages.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val currentStatus by doctorStatusViewModel.status.collectAsState()
    val s3Config by voice2RxViewModel.s3ConfigResponse.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedPatient by remember { mutableStateOf<PatientEntity?>(null) }

    val selectedRecords = remember { mutableStateListOf<RecordModel?>() }
    val uniqueRecords = selectedRecords.toMutableSet()

    var chatContext by remember {
        mutableStateOf(viewModel.currentChatContext)
    }
    var screenTitle by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current

    val onDismissBottomSheet: () -> Unit = {
        scope.launch {
            showBottomSheet = false
            viewModel.currentSheetContent = null
        }
    }
    var sessionId by remember {
        mutableStateOf(navData.sessionId)
    }
    val visitId: String? by remember {
        mutableStateOf(navData.visitId)
    }
    val openType: String? by remember {
        mutableStateOf(navData.openType)
    }
    LaunchedEffect(Unit) {
        (context.applicationContext as IAmCommon).setSessionListener(
            Voice2RxSessionManager(
                context = context,
                ekaChatViewModel = viewModel,
                voice2RxViewModel = voice2RxViewModel,
                doctorStatusViewModel = doctorStatusViewModel
            )
        )
    }

    LaunchedEffect(Unit) {
        if (!navData.patientId.isNullOrEmpty()) {
            selectedPatient = patientViewModel.getPatientByOid(navData.patientId)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.updateTextInputState("")
        if (sessionId.isNullOrEmpty()) {
            sessionId = Utils.getNewSessionId()
            viewModel.updateSessionId(sessionId.toString())
            viewModel.getSessionMessages(sessionId.toString())
        } else {
            viewModel.updateSessionId(sessionId.toString())
            viewModel.getSessionMessages(sessionId.toString())
        }
        viewModel.sendButtonEnabled = true
    }

    LaunchedEffect(chatContext, selectedPatient) {
        if (chatContext == null && selectedPatient != null) {
            chatContext = ChatUtils.buildChatContext(selectedPatient)
        }
        viewModel.currentChatContext = chatContext
    }

    LaunchedEffect(sessionMessages) {
        if (sessionMessages.messageEntityResp.isNullOrEmpty()) {
            chatContext = ChatUtils.buildChatContext(selectedPatient)
            if (chatContext == null) {
                val contextVal = navData.chatContext
                if (!contextVal.isNullOrEmpty()) {
                    chatContext = Gson().fromJson(contextVal, ChatContext::class.java)
                }
            }
            viewModel.currentChatContext = chatContext
            screenTitle = context.getString(R.string.new_chat)
        } else {
            chatContext =
                ChatUtils.getChatContextFromString(sessionMessages.messageEntityResp.firstOrNull()?.message?.chatContext)
            viewModel.currentChatContext = chatContext
            screenTitle = when (sessionMessages.messageEntityResp.lastOrNull()?.message?.msgType) {
                MessageType.TEXT.stringValue -> {
                    sessionMessages.messageEntityResp.lastOrNull()?.message?.messageText
                        ?: "Conversation"
                }

                null -> {
                    context.getString(R.string.new_chat)
                }

                else -> {
                    "Conversation"
                }
            }
        }
    }

    val onOpenBottomSheet: () -> Unit = {
        scope.launch {
            showBottomSheet = true
        }
    }

    var showPermissionDialog by remember {
        mutableStateOf(false)
    }
    if (showPermissionDialog) {
        MicrophonePermissionAlertDialog(
            onDismiss = {
                showPermissionDialog = false
            },
            onConfirm = {
                showPermissionDialog = false
            }
        )
    }

    LaunchedEffect(Unit) {
        if (!openType.isNullOrEmpty() && !viewModel.isVoice2RxRecording) {
            when (openType) {
                "V2RX" -> {
                    processVoice2RxClick(
                        viewModel = viewModel,
                        context = context,
                        onConfirm = {
                            viewModel.currentSheetContent =
                                EkaChatSheetContent.Voice2RxInitialBottomSheet
                            onOpenBottomSheet()
                        },
                        onPermissionNotGiven = {
                            showPermissionDialog = true
                        }
                    )
                }

                else -> {}
            }
        }
    }

    val onVoice2RxClick: (cta: CTA) -> Unit = { cta ->
        viewModel.currentChatContext = chatContext
        var consultationData: ConsultationData? = null
        var mode = Voice2RxType.DICTATION
        when (cta.action) {
            ActionType.ON_VOICE_TO_RX_CONVERSATION_MODE.stringValue -> {
                viewModel.voice2RxContext = chatContext?.patientName
                    ?: Voice2RxType.CONSULTATION.value.capitalizeFirstWordOfEachString()
                doctorStatusViewModel.resetElapsedSeconds()
                doctorStatusViewModel.updateConsultationState(
                    state = DocStatus.ConsultationStatus(ConsultationState.WAITING_TIMER)
                )
                mode = Voice2RxType.CONSULTATION
            }

            ActionType.ON_VOICE_TO_RX_DICTATION_MODE.stringValue -> {
                viewModel.voice2RxContext = chatContext?.patientName
                    ?: Voice2RxType.DICTATION.value.capitalizeFirstWordOfEachString()
                doctorStatusViewModel.resetElapsedSeconds()
                doctorStatusViewModel.updateConsultationState(
                    state = DocStatus.ConsultationStatus(ConsultationState.WAITING_TIMER)
                )
                mode = Voice2RxType.DICTATION
            }
        }
        consultationData = ConsultationData(
            voiceContext = viewModel.voice2RxContext,
            mode = mode,
            sessionId = Voice2RxUtils.generateNewSessionId(),
            sessionIdentity = selectedPatient?.oid ?: "",
        )
        doctorStatusViewModel.consultationData = consultationData
        viewModel.consultationData = consultationData
        (context.applicationContext as IAmCommon).setSessionListener(
            Voice2RxSessionManager(
                context = context,
                ekaChatViewModel = viewModel,
                voice2RxViewModel = voice2RxViewModel,
                doctorStatusViewModel = doctorStatusViewModel
            )
        )

        viewModel.startVoiceSession(
            voice2RxSessionId = consultationData.sessionId,
            patientId = chatContext?.patientId,
            visitId = visitId,
            s3Config = s3Config,
            mode = mode,
        )

        onDismissBottomSheet()
    }

    BackHandler(enabled = showBottomSheet || isInputBottomSheetVisible) {
        isInputBottomSheetVisible = false
        onDismissBottomSheet()
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissBottomSheet,
            sheetState = sheetState,
            containerColor = DarwinTouchNeutral0,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = { DefaultDragHandle() },
            modifier = Modifier.heightIn(max = screenHeight.times(0.95f))
        ) {
            when (viewModel.currentSheetContent) {
                is EkaChatSheetContent.Voice2RxInitialBottomSheet -> Voice2RxInitialBottomSheet(
                    onClick = { cta ->
                        onVoice2RxClick(cta)
                    }
                )

                is EkaChatSheetContent.Voice2RxPatientBottomSheet -> PatientSelectorScreen(
                    onClose = {
                        onDismissBottomSheet()
                    },
                    onPatientSelected = {
                        selectedPatient = it
                        chatContext = ChatUtils.buildChatContext(selectedPatient)
                        viewModel.currentChatContext = chatContext
                        onDismissBottomSheet()
                    }
                )

                is EkaChatSheetContent.Voice2RxMedicalBottomSheet ->
                    MedicalRecordsSelectorScreen(
                        patientEntity = selectedPatient,
                        selectedRecords = { listOfRecords ->
                            uniqueRecords.clear()
                            selectedRecords.clear()
                            uniqueRecords.addAll(listOfRecords)
                            selectedRecords.addAll(uniqueRecords)
                            viewModel.updateSelectedRecords(uniqueRecords.toList())
                        },
                        chatContext = chatContext,
                        onClick = { cta ->
                            when (cta.action) {
                                ActionType.ON_BACK.stringValue -> {
                                    onDismissBottomSheet()
                                }
                            }
                        }
                    )

                is EkaChatSheetContent.Voice2RxPatientDetailBottomSheet -> {
                    Voice2RxPatientDetailScreen()
                }

                else -> {}
            }
        }
    }
    Scaffold(
        containerColor = DarwinTouchNeutral0,
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        topBar = {
            EkaChatBotTopBar(
                consultationStarted = currentStatus is DocStatus.ConsultationStatus,
                hasClinicalNotes = false,
                title = screenTitle.ifEmpty { stringResource(id = R.string.new_chat) },
                subTitle = "ParrotLet 1.0",
                onClick = { cta ->
                    when (cta.action) {
                        ActionType.ON_BACK.stringValue -> {
                            onBackClick()
                        }

                        ActionType.OPEN_CLINICAL_NOTES.stringValue -> {
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomBarMainScreen(
                chatContext = chatContext,
                viewModel = viewModel,
                onOpenBottomSheet = onOpenBottomSheet,
                onClick = {
                    when (it.action) {
                        ActionType.SHOW_INPUT_BOTTOM_SHEET.stringValue -> {
                            isInputBottomSheetVisible = true
                        }
                    }
                },
                isInputBottomSheetVisible = isInputBottomSheetVisible
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            EkaNewChatContent(
                viewModel = viewModel,
                voice2RxViewModel = voice2RxViewModel,
                sessionId = sessionId.toString(),
                onSuggestionClick = {
                    val docId = OrbiUserManager.getUserTokenData()?.oid
                    viewModel.askNewQueryFireStore(
                        query = it.label,
                        chatContext = chatContext,
                        docId = docId!!,
                        ownerId = ChatUtils.getOwnerId(),
                        userHash = OrbiUserManager.getChatUserHash(docId)!!,
                        selectedRecords = emptyList()
                    )
                    viewModel.updateSelectedRecords(emptyList())
                },
                s3Config = s3Config,
                onClick = { cta ->
                    when (cta.action) {
                        ActionType.START_CONSULTATION.stringValue -> {
                            doctorStatusViewModel.updateConsultationState(
                                state = DocStatus.ConsultationStatus(ConsultationState.WAITING_TIMER)
                            )
                        }

                        ActionType.ON_VOICE_2_RX_CLICK.stringValue -> {
                            processVoice2RxClick(
                                viewModel = viewModel,
                                context = context,
                                onConfirm = {
                                    viewModel.currentSheetContent =
                                        EkaChatSheetContent.Voice2RxInitialBottomSheet
                                    onOpenBottomSheet()
                                },
                                onPermissionNotGiven = {
                                    showPermissionDialog = true
                                }
                            )
                        }
                    }
                },
                openTranscriptScreen = openTranscriptScreen
            )
        }
    }
}

private fun processVoice2RxClick(
    onConfirm: () -> Unit,
    onPermissionNotGiven: () -> Unit,
    viewModel: EkaChatViewModel,
    context: Context,
) {
    if (!PermissionUtils.hasRecordAudioPermission(context = context)) {
        onPermissionNotGiven()
        return
    }
    if (viewModel.isVoice2RxRecording || viewModel.isVoiceToTextRecording) {
        viewModel.showToast("Recording is in progress!")
        return
    }
    onConfirm()
}

@Composable
private fun DefaultDragHandle() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .alpha(0.4f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(DarwinTouchNeutral600)
        )
    }
}
