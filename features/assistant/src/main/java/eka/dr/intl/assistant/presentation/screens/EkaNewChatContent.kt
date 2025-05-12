package eka.dr.intl.assistant.presentation.screens

import android.content.Context
import android.graphics.Picture
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.eka.conversation.data.local.db.entities.MessageEntity
import com.eka.conversation.data.local.db.entities.models.MessageRole
import com.eka.voice2rx_sdk.common.Voice2RxUtils
import com.eka.voice2rx_sdk.data.local.models.Voice2RxSessionStatus
import com.eka.voice2rx_sdk.sdkinit.AwsS3Configuration
import com.google.gson.Gson
import dev.jeziellago.compose.markdowntext.MarkdownText
import eka.dr.intl.assistant.data.remote.dto.response.AwsS3ConfigResponseOld
import eka.dr.intl.assistant.presentation.components.ChatBubbleLeft
import eka.dr.intl.assistant.presentation.components.ChatBubbleProcessing
import eka.dr.intl.assistant.presentation.components.ChatBubbleRecordingComponent
import eka.dr.intl.assistant.presentation.components.ChatBubbleRight
import eka.dr.intl.assistant.presentation.components.EkaBotTag
import eka.dr.intl.assistant.presentation.components.EkaChatContent
import eka.dr.intl.assistant.presentation.components.SuggestionsComponent
import eka.dr.intl.assistant.presentation.models.ChatMessage
import eka.dr.intl.assistant.presentation.models.SuggestionModel
import eka.dr.intl.assistant.presentation.states.SessionMessagesState
import eka.dr.intl.assistant.presentation.viewmodel.EkaChatViewModel
import eka.dr.intl.assistant.presentation.viewmodel.Voice2RxViewModel
import eka.dr.intl.assistant.utility.ActionType
import eka.dr.intl.assistant.utility.ChatUtils
import eka.dr.intl.assistant.utility.MessageType
import eka.dr.intl.assistant.utility.Voice2RxConstants
import eka.dr.intl.common.IAmCommon
import eka.dr.intl.common.data.dto.response.CTA
import eka.dr.intl.common.utility.NavigateToTranscriptScreen
import eka.dr.intl.ekatheme.color.DarwinTouchGreen
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral100
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral50
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.ekatheme.color.DarwinTouchRed
import eka.dr.intl.ekatheme.color.DarwinTouchRedBgLight
import eka.dr.intl.ekatheme.color.DarwinTouchYellow
import eka.dr.intl.icons.R
import eka.dr.intl.typography.touchBodyBold
import eka.dr.intl.typography.touchBodyRegular
import eka.dr.intl.typography.touchLabelRegular
import org.json.JSONObject
import org.koin.androidx.compose.koinViewModel

@Composable
fun EkaNewChatContent(
    viewModel: EkaChatViewModel,
    voice2RxViewModel: Voice2RxViewModel,
    onSuggestionClick: (SuggestionModel) -> Unit,
    sessionId: String,
    s3Config: AwsS3ConfigResponseOld?,
    onClick: (CTA) -> Unit,
    openTranscriptScreen: NavigateToTranscriptScreen
) {
    val sessionMessages by viewModel.sessionMessages.collectAsState()
    val suggestionList by viewModel.suggestionList.collectAsState()
    val listState = rememberLazyListState()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopAudio()
        }
    }

    LaunchedEffect(sessionMessages) {
        if (sessionMessages.messageEntityResp.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }
    LaunchedEffect(Unit) {
        if (sessionId.isNullOrEmpty()) {
            viewModel.updateSessionId(session = viewModel.sessionId)
            viewModel.getSessionMessages(sessionId = viewModel.sessionId)
        } else {
            viewModel.updateSessionId(session = sessionId)
            viewModel.getSessionMessages(sessionId = sessionId)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(color = DarwinTouchNeutral50)
    ) {
        if (sessionMessages.messageEntityResp.isNotEmpty()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, bottom = 16.dp, end = 8.dp),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                if (sessionMessages.messageEntityResp.isNotEmpty() && suggestionList.isNotEmpty() && sessionMessages.messageEntityResp.first().message.role == MessageRole.AI) {
                    item {
                        SuggestionsComponent(
                            suggestionList = suggestionList,
                            onSuggestionClicked = {
                                onSuggestionClick(it)
                            },
                            showLeftIcon = true,
                        )
                    }
                }
                itemsIndexed(
                    items = sessionMessages.messageEntityResp,
                    key = { _, chatMessage -> chatMessage.message.msgId }) { index, chatMessage ->

                    if (showLoader(
                            sessionMessages.messageEntityResp,
                            chatMessage
                        ) && viewModel.isQueryResponseLoading
                    ) {
                        ChatBubbleProcessing()
                    }

                    ChatMessageComponent(
                        chatMessage = chatMessage,
                        viewModel = viewModel,
                        voice2RxViewModel = voice2RxViewModel,
                        sessionMessages = sessionMessages,
                        s3Config = s3Config,
                        openTranscriptScreen = openTranscriptScreen
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                    )
                }
            }
        } else {
            EkaChatContent(
                modifier = Modifier,
                onClick = onClick
            )
        }
    }
}

@Composable
fun ChatMessageComponent(
    chatMessage: ChatMessage,
    viewModel: EkaChatViewModel,
    voice2RxViewModel: Voice2RxViewModel,
    sessionMessages: SessionMessagesState,
    s3Config: AwsS3ConfigResponseOld?,
    openTranscriptScreen: NavigateToTranscriptScreen
) {
    val message = chatMessage.message
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val transcript by voice2RxViewModel.getTranscript.collectAsState()
    val transcriptData = if(chatMessage.voice2RxSession?.transcript != null) {
        chatMessage.voice2RxSession.transcript
    }else transcript
    when (message.role) {
        MessageRole.USER -> {
            ChatBubbleRight(
                chatMessage = chatMessage,
                onClick = {
                    if (message.msgType == MessageType.VOICE_2_RX_PRESCRIPTION_DRAFT.stringValue) {
                        chatMessage.voice2RxSession?.sessionId?.let {
                            openTranscriptScreen(
                                it,
                                transcriptData
                            )
                        }
                    }
                }
            )
        }

        MessageRole.AI -> {
            when (message.msgType) {
                MessageType.VOICE_2_RX_PRESCRIPTION_DRAFT.stringValue -> {
                    var tag = "Draft"
                    var iconTint = DarwinTouchYellow
                    if (chatMessage.voice2RxSession != null && chatMessage.voice2RxSession.status == Voice2RxSessionStatus.COMPLETED) {
                        tag = ""
                        iconTint = DarwinTouchGreen
                    }
                    ChipComponent(label = "Conversation ended")
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                    )
                    ChatBubbleRecordingComponent(
                        topStartPadding = 16.dp,
                        borderStroke = BorderStroke(
                            width = 1.dp,
                            color = DarwinTouchPrimary
                        ),
                        containerColor = DarwinTouchNeutral0,
                        title = "View clinical notes",
                        tagEnabled = true,
                        tagText = tag,
                        icon = R.drawable.ic_circle_waveform_lines_regular,
                        iconTint = iconTint,
                        date = ChatUtils.getTimeStampString(message.createdAt),
                        titleStyle = touchBodyBold,
                        onClick = {
                            chatMessage.voice2RxSession?.sessionId?.let {
                                openTranscriptScreen(
                                    it,
                                    transcriptData
                                )
                            }
                        },
                        isFirstMessage = shouldShowLeftIcon(
                            sessionMessages.messageEntityResp,
                            currentMessage = message
                        )
                    )
                }

                MessageType.VOICE_2_RX_ERROR.stringValue -> {
                    val tag = "Tap to Try again"
                    val iconTint = DarwinTouchRed
                    ChipComponent(label = "Conversation ended")
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                    )
                    ChatBubbleRecordingComponent(
                        topStartPadding = 16.dp,
                        borderStroke = BorderStroke(
                            width = 1.dp,
                            color = DarwinTouchRed
                        ),
                        containerColor = DarwinTouchNeutral0,
                        title = "Failed to analyse",
                        tagEnabled = true,
                        tagText = tag,
                        icon = R.drawable.ic_circle_info_regular,
                        iconTint = iconTint,
                        date = "",
                        tagBackgroundColor = DarwinTouchRedBgLight,
                        tagTextColor = DarwinTouchRed,
                        titleStyle = touchBodyBold,
                        onClick = {
                            (context.applicationContext as IAmCommon).retrySession(
                                sessionId = chatMessage.message.messageText.toString(),
                                context = context,
                                s3Config = AwsS3Configuration(
                                    accessKey = s3Config?.credentials?.accessKeyId
                                        ?: "",
                                    secretKey = s3Config?.credentials?.secretKey
                                        ?: "",
                                    sessionToken = s3Config?.credentials?.sessionToken
                                        ?: "",
                                    bucketName = Voice2RxConstants.BUCKET_NAME
                                ),
                                messageJson = Gson().toJson(chatMessage.message)
                            )
                        },
                        isFirstMessage = shouldShowLeftIcon(
                            sessionMessages.messageEntityResp,
                            currentMessage = message
                        )
                    )
                }

                MessageType.AUDIO.stringValue -> {
                    val fileName = Voice2RxUtils.getFilePath(
                        context,
                        message.messageText.toString()
                    )
                    val isPlaying =
                        viewModel.playingSessionId == message.messageText
                    val icon = if (isPlaying) {
                        R.drawable.ic_pause_solid
                    } else {
                        R.drawable.ic_play_solid
                    }
                    ChatBubbleRecordingComponent(
                        topStartPadding = 0.dp,
                        containerColor = DarwinTouchNeutral100,
                        title = "Recording",
                        subtitle = ChatUtils.formatDuration(
                            ChatUtils.getAudioFileDuration(
                                fileName
                            )
                        ),
                        icon = icon,
                        iconTint = DarwinTouchPrimary,
                        date = ChatUtils.getTimeStampString(message.createdAt),
                        titleStyle = touchBodyRegular,
                        onClick = {
                            if (isPlaying) {
                                viewModel.stopAudio()
                            } else {
                                viewModel.playAudio(
                                    sessionId = message.messageText.toString(),
                                    filePath = fileName
                                )
                            }
                        },
                        isFirstMessage = shouldShowLeftIcon(
                            sessionMessages.messageEntityResp,
                            currentMessage = message
                        )
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                    )
                    ChipComponent(label = "Conversation  started")
                }

                else -> {
                    ChatBubbleLeft(
                        message = chatMessage,
                        value = message.messageText.toString(),
                        showResponseButtons = shouldShowResponseButtons(
                            viewModel = viewModel,
                            messages = sessionMessages.messageEntityResp,
                            message = chatMessage
                        ),
                        onClick = { cta ->
                            handleMessageCTA(
                                cta = cta,
                                chatMessage = chatMessage,
                                clipboardManager = clipboardManager,
                                context = context,
                                viewModel = viewModel
                            )
                        },
                        isFirstMessage = shouldShowLeftIcon(
                            sessionMessages.messageEntityResp,
                            currentMessage = message
                        )
                    )
                }
            }

        }

        MessageRole.CUSTOM -> {
        }

        else -> {
        }
    }
}

fun shouldShowResponseButtons(
    viewModel: EkaChatViewModel,
    messages: List<ChatMessage>,
    message: ChatMessage
): Boolean {
    return viewModel.sendButtonEnabled || !isLastMessage(messages, message)
}

fun isLastMessage(messages: List<ChatMessage>, message: ChatMessage): Boolean {
    return messages.first().message.msgId == message.message.msgId
}

fun handleMessageCTA(
    context: Context,
    viewModel: EkaChatViewModel,
    chatMessage: ChatMessage,
    clipboardManager: ClipboardManager,
    cta: CTA
) {
    when (cta.action) {
        ActionType.ON_COPY_CLICKED.stringValue -> {
            val params = JSONObject()
            params.put("type", "copy")
            params.put("session_id", chatMessage.message.sessionId)
            params.put("text", chatMessage.message.messageText)
            clipboardManager.setText(AnnotatedString(chatMessage.message.messageText.toString()))
            viewModel.showToast("Copied to Clipboard.")
        }

        ActionType.ON_SHARE_CLICKED.stringValue -> {
            val params = JSONObject()
            params.put("type", "sharepdf")
            params.put("session_id", chatMessage.message.sessionId)
            params.put("text", chatMessage.message.messageText)

            viewModel.createPDF(context = context as? ComponentActivity, chatMessage = chatMessage)
        }

        ActionType.ON_POSITIVE_REVIEW_CLICKED.stringValue -> {
            val params = JSONObject()
            params.put("type", "good")
            params.put("session_id", chatMessage.message.sessionId)
            params.put("text", chatMessage.message.messageText)

            viewModel.showToast("Review Submitted.")
        }

        ActionType.ON_NEGETIVE_REVIEW_CLICKED.stringValue -> {
            val params = JSONObject()
            params.put("type", "bad")
            params.put("session_id", chatMessage.message.sessionId)
            params.put("text", chatMessage.message.messageText)

            viewModel.showToast("Review Submitted.")
        }
    }
}

@Composable
fun SharePdfContent(
    chatMessage: ChatMessage,
    context: Context,
    onCompletion: (Picture) -> Unit
) {
    val picture by remember { mutableStateOf(Picture()) }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .drawWithCache {
                val width = this.size.width.toInt()
                val height = this.size.height.toInt()
                onDrawWithContent {
                    val pictureCanvas =
                        androidx.compose.ui.graphics.Canvas(
                            picture.beginRecording(
                                width,
                                height
                            )
                        )
                    draw(this, this.layoutDirection, pictureCanvas, this.size) {
                        this@onDrawWithContent.drawContent()
                    }
                    picture.endRecording()
                    onCompletion(picture)
                }
            }
    ) {
        Column {
            MarkdownText(
                markdown = chatMessage.message.messageText.toString(),
                enableSoftBreakAddsNewLine = true,
                style = touchBodyRegular,
                color = DarwinTouchNeutral1000
            )
        }
    }
}

@Composable
fun ChipComponent(label: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        EkaBotTag(
            tagName = label,
            backgroundColor = DarwinTouchNeutral100,
            style = touchLabelRegular
        )
    }
}

fun showLoader(messages: List<ChatMessage>, message: ChatMessage): Boolean {
    return message.message.role == MessageRole.USER && messages.first().message.msgId == message.message.msgId
}

fun shouldShowLeftIcon(messages: List<ChatMessage>, currentMessage: MessageEntity): Boolean {
    return currentMessage.role == MessageRole.AI && ((currentMessage.msgId == 0) || (messages.filter { it.message.msgId == currentMessage.msgId - 1 }
        .first().message.role != MessageRole.AI))
}