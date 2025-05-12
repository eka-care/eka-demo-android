package eka.dr.intl.assistant.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.eka.conversation.common.PermissionUtils
import com.eka.conversation.common.Response
import com.eka.conversation.common.Utils
import eka.care.documents.ui.presentation.model.CTA
import eka.dr.intl.assistant.presentation.viewmodel.EkaChatViewModel
import eka.dr.intl.assistant.utility.ActionType
import eka.dr.intl.assistant.utility.ChatUtils
import eka.dr.intl.assistant.utility.EkaChatSheetContent
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.data.dto.response.ChatContext
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral50
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BottomBarMainScreen(
    chatContext: ChatContext? = null,
    viewModel: EkaChatViewModel,
    onOpenBottomSheet: () -> Unit,
    onClick: (CTA) -> Unit,
    isInputBottomSheetVisible: Boolean
) {
    val sessionMessages by viewModel.sessionMessages.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current.applicationContext
    val selectedRecords by viewModel.selectedRecords.collectAsState()
    val scope = rememberCoroutineScope()

    fun hideKeyboardAndExecute(action: suspend () -> Unit) {
        scope.launch {
            keyboardController?.hide()
            delay(300)
            action()
        }
    }

    fun hideKeyboardAndShowComposable(showComposable: () -> Unit) {
        scope.launch {
            keyboardController?.hide()
            delay(300) // Changed from 3000 to match the other function
            showComposable()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isInputBottomSheetVisible) {
            EkaChatBottom(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarwinTouchNeutral50)
                    .padding(bottom = 16.dp)
                    .imePadding(),
                showPatientSelection = sessionMessages.messageEntityResp.isEmpty(),
                selectedPatientName = chatContext?.patientName,
                onMicrophoneClick = {
                    if (PermissionUtils.hasRecordAudioPermission(context) && Utils.isNetworkAvailable(
                            context
                        )
                    ) {
                        hideKeyboardAndShowComposable {
                            viewModel.isVoiceToTextRecording = true
                            onClick(CTA(action = ActionType.SHOW_INPUT_BOTTOM_SHEET.stringValue))
                        }
                    } else if (!Utils.isNetworkAvailable(context)) {
                        viewModel.showToast("Internet not available.")
                    } else {
                        viewModel.showToast("Microphone permission not granted.")
                    }
                },
                isMicrophoneRecording = viewModel.isVoiceToTextRecording,
                isVoice2RxRecording = viewModel.isVoice2RxRecording,
                viewModel = viewModel,
                onClick = { cta ->
                    when (cta.action) {
                        ActionType.ON_GALLERY_CLICK.stringValue -> {
                            hideKeyboardAndExecute {
                                viewModel.currentSheetContent =
                                    EkaChatSheetContent.Voice2RxMedicalBottomSheet
                                onOpenBottomSheet()
                            }
                        }

                        ActionType.ON_PATIENT_CLICK.stringValue -> {
                            hideKeyboardAndExecute {
                                if (sessionMessages.messageEntityResp.isEmpty()) {
                                    viewModel.currentSheetContent =
                                        EkaChatSheetContent.Voice2RxPatientBottomSheet
                                    onOpenBottomSheet()
                                }
                            }
                        }

                        ActionType.OPEN_INPUT_BOTTOM_SHEET.stringValue -> {
                            onClick(CTA(action = ActionType.SHOW_INPUT_BOTTOM_SHEET.stringValue))
                        }

                        ActionType.ON_VOICE_2_RX_CLICK.stringValue -> {
                            viewModel.currentSheetContent =
                                EkaChatSheetContent.Voice2RxInitialBottomSheet
                            onOpenBottomSheet()
                        }
                    }
                }
            )
        }
        AnimatedVisibility(
            visible = isInputBottomSheetVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(500)
            )
        ) {
            EkaChatInputBottom(
                patientTagName = chatContext?.patientName,
                showPatientSelection = sessionMessages.messageEntityResp.isEmpty(),
                onQueryText = {
//                    if (OrbiUserManager.getUserRole() == "DOCTOR") {
                    val docId = OrbiUserManager.getUserTokenData()?.oid
                    if (docId.isNullOrEmpty()) {
                        return@EkaChatInputBottom
                    }
                    if (it.isNotEmpty() || (!selectedRecords.isNullOrEmpty())) {
                        viewModel.askNewQueryFireStore(
                            query = it,
                            chatContext = chatContext,
                            docId = docId,
                            ownerId = ChatUtils.getOwnerId(),
                            userHash = OrbiUserManager.getChatUserHash(docId)!!,
                            selectedRecords = selectedRecords
                        )
                        viewModel.updateSelectedRecords(emptyList())
                    } else {
                        viewModel.showToast("Please enter a query.")
                    }
//                    }
                },
                viewModel = viewModel,
                isMicrophoneRecording = viewModel.isVoiceToTextRecording,
                isVoice2RxRecording = viewModel.isVoice2RxRecording,
                onMicrophoneClick = {
                    if (PermissionUtils.hasRecordAudioPermission(context) && Utils.isNetworkAvailable(
                            context
                        )
                    ) {
                        keyboardController?.hide()
                        viewModel.isVoiceToTextRecording = !viewModel.isVoiceToTextRecording
                    } else if (!Utils.isNetworkAvailable(context)) {
                        viewModel.showToast("Internet not available.")
                    } else {
                        viewModel.showToast("Microphone permission not granted.")
                    }
                },
                textFieldEnabled = !viewModel.isVoiceToTextRecording,
                focusRequester = focusRequester,
                onClick = { cta ->
                    when (cta.action) {
                        ActionType.START_CHAT.stringValue -> {

                        }

                        ActionType.ON_GALLERY_CLICK.stringValue -> {
                            hideKeyboardAndExecute {
                                viewModel.currentSheetContent =
                                    EkaChatSheetContent.Voice2RxMedicalBottomSheet
                                onOpenBottomSheet()
                            }
                        }

                        ActionType.ON_PATIENT_CLICK.stringValue -> {
                            if (sessionMessages.messageEntityResp.isNullOrEmpty()) {
                                hideKeyboardAndExecute {
                                    viewModel.currentSheetContent =
                                        EkaChatSheetContent.Voice2RxPatientBottomSheet
                                    onOpenBottomSheet()
                                }
                            }
                        }

                        ActionType.ON_VOICE_2_RX_CLICK.stringValue -> {
                            hideKeyboardAndExecute {
                                viewModel.currentSheetContent =
                                    EkaChatSheetContent.Voice2RxInitialBottomSheet
                                onOpenBottomSheet()
                            }
                        }
                    }
                })
        }
        AnimatedVisibility(
            visible = viewModel.isVoiceToTextRecording,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
        ) {
            AudioFeatureLayout(viewModel = viewModel) { response ->
                viewModel.isVoiceToTextRecording = false
                when (response) {
                    is Response.Loading -> {
                    }

                    is Response.Success -> {
                        val transcribedText = response.data.toString()
                        viewModel.updateTextInputState(transcribedText)
                        viewModel.clearRecording()
                        focusRequester.requestFocus()
                    }

                    is Response.Error -> {
                        val errorMsg = response.message.toString()
                        viewModel.showToast(errorMsg)
                        viewModel.clearRecording()
                    }
                }
            }
        }
    }
}