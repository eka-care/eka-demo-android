package eka.dr.intl.assistant.presentation.viewmodel

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Picture
import android.graphics.pdf.PdfDocument
import android.media.MediaPlayer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingSource
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.eka.conversation.ChatInit
import com.eka.conversation.common.Response
import com.eka.conversation.common.Utils
import com.eka.conversation.common.models.NetworkConfiguration
import com.eka.conversation.data.local.db.entities.MessageEntity
import com.eka.conversation.data.local.db.entities.models.MessageFileType
import com.eka.conversation.data.local.db.entities.models.MessageRole
import com.eka.conversation.features.audio.AndroidAudioRecorder
import com.eka.voice2rx_sdk.data.local.models.Voice2RxSessionStatus
import com.eka.voice2rx_sdk.data.local.models.Voice2RxType
import com.eka.voice2rx_sdk.sdkinit.AwsS3Configuration
import com.eka.voice2rx_sdk.sdkinit.Voice2Rx
import com.google.gson.Gson
import eka.care.documents.Document
import eka.care.documents.ui.presentation.model.RecordModel
import eka.dr.intl.assistant.BuildConfig
import eka.dr.intl.assistant.data.remote.dto.response.AwsS3ConfigResponseOld
import eka.dr.intl.assistant.data.remote.dto.response.UserHashResponse
import eka.dr.intl.assistant.data.repository.EkaChatRepositoryImpl
import eka.dr.intl.assistant.domain.repository.EkaChatRepository
import eka.dr.intl.assistant.presentation.models.ChatMessage
import eka.dr.intl.assistant.presentation.models.ChatSession
import eka.dr.intl.assistant.presentation.models.SuggestionModel
import eka.dr.intl.assistant.presentation.screens.BotViewMode
import eka.dr.intl.assistant.presentation.screens.SharePdfContent
import eka.dr.intl.assistant.presentation.states.SessionMessagesState
import eka.dr.intl.common.data.dto.response.ChatContext
import eka.dr.intl.assistant.utility.ChatUtils
import eka.dr.intl.assistant.utility.EkaChatSheetContent
import eka.dr.intl.assistant.utility.EkaResponse
import eka.dr.intl.assistant.utility.MessageType
import eka.dr.intl.assistant.utility.MessageTypeMapping
import eka.dr.intl.assistant.utility.pdf.GeneratePdfLibrary
import eka.dr.intl.assistant.utility.pdf.PageSize
import eka.dr.intl.assistant.utility.pdf.PdfUtils
import eka.dr.intl.common.IAmCommon
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.PageIdentifier
import eka.dr.intl.common.data.dto.response.ConsultationData
import eka.dr.intl.patients.data.local.entity.PatientEntity
import eka.dr.intl.patients.domain.repository.PatientStoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class EkaChatViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private var chatRepository: EkaChatRepository = EkaChatRepositoryImpl()
    private var patientRepository: PatientStoreRepository? = null

    private val _userHashResponse =
        MutableStateFlow<EkaResponse<UserHashResponse>>(EkaResponse.Loading())

    var currentSheetContent by mutableStateOf<EkaChatSheetContent?>(null)
    var sessionId by mutableStateOf("")
    var sendButtonEnabled by mutableStateOf(true)

    private val _sessionMessages =
        MutableStateFlow(SessionMessagesState(isLoading = true, messageEntityResp = emptyList()))
    val sessionMessages = _sessionMessages.asStateFlow()

    var voice2RxContext by mutableStateOf<String>("")
    var currentChatContext by mutableStateOf<ChatContext?>(null)
    var consultationData by mutableStateOf<ConsultationData?>(null)

    private var _selectedRecords = MutableStateFlow<ArrayList<RecordModel?>>(ArrayList())
    var selectedRecords = _selectedRecords.asStateFlow()

    private lateinit var audioRecorder: AndroidAudioRecorder

    private var currentAudioFile: File? = null

    private val _chatSessions = MutableStateFlow<List<ChatSession>>(emptyList())
    val chatSessions = _chatSessions.asStateFlow()

    private val _isSessionsLoading = MutableStateFlow<Boolean>(false)
    val isSessionLoading = _isSessionsLoading.asStateFlow()

    private val _currentPatient = MutableStateFlow<PagingSource<Int, PatientEntity>?>(null)
    val currentPatient = _currentPatient.asStateFlow()

    private val _groupedSessionsByContext =
        MutableStateFlow<Map<ChatContext, List<ChatSession>>>(emptyMap())
    val groupedSessionsByContext = _groupedSessionsByContext.asStateFlow()

    private val _currentTranscribeData = MutableStateFlow<Response<String>>(Response.Loading())
    val currentTranscribeData = _currentTranscribeData.asStateFlow()

    private val _botViewMode = MutableStateFlow(BotViewMode.ALL_CHATS)
    val botViewMode = _botViewMode.asStateFlow()

    private val _textInputState = MutableStateFlow("")
    val textInputState = _textInputState.asStateFlow()

    var job: Job? = null
    private var mediaPlayer: MediaPlayer? = null
    var playingSessionId by mutableStateOf("")

    var isVoice2RxRecording: Boolean by mutableStateOf(false)
    var isVoiceToTextRecording: Boolean by mutableStateOf(false)
    var isQueryResponseLoading: Boolean by mutableStateOf(false)

    init {
        ChatInit.initialize(
            context = app,
            chatInitConfiguration = null
        )
        initializeVoice2RxSDK()
    }

    fun updateSelectedRecords(records: List<RecordModel?>) {
        _selectedRecords.value = ArrayList(records)
    }

    fun removeRecord(record: RecordModel?) {
        _selectedRecords.value =
            ArrayList(_selectedRecords.value.filterNot { it?.documentId == record?.documentId })
    }

    fun updateBotViewMode(newMode: BotViewMode) {
        _botViewMode.value = newMode
    }

    fun getUserHash(oid: String?, uuid: String?) {
        viewModelScope.launch {
            if (oid != null && uuid != null) {
                val response = chatRepository.getUserHash(oid, uuid)
                when (response) {
                    is EkaResponse.Loading -> {
                    }

                    is EkaResponse.Success -> {
                        saveUserHash(response.data, oid)
                    }

                    is EkaResponse.Error -> {
                    }

                    else -> {}
                }
                _userHashResponse.value = response
            }
        }
    }

    private fun saveUserHash(userHashResponse: UserHashResponse?, selectedOid: String) {
        userHashResponse?.let {
            if (it.userHash != null) {
                OrbiUserManager.saveChatUserHash(it.userHash, selectedOid)
            }
        }
    }

    fun getPatient(patientId: String): PagingSource<Int, PatientEntity>? {
        return patientRepository?.getPatientByIds(listOf(patientId))
    }

    fun updateTextInputState(newValue: String) {
        _textInputState.value = newValue
    }

    fun getTranscribeDataFromAudioFile(audioFile: File, onResult: (Response<String>) -> Unit) {
        viewModelScope.launch {
            val response = chatRepository.getTranscribeDataFromAudioFile(audioFile)
            when (response) {
                is EkaResponse.Loading -> {
                }

                is EkaResponse.Success -> {
                    onResult(Response.Success(response.data?.text.toString()))
                }

                is EkaResponse.Error -> {
                    onResult(Response.Error("Error Occurred! \n" + response.message.toString()))
                }

                else -> {}
            }
        }
    }

    fun updateSessionId(session: String) {
        sessionId = session
        clearSessionMessages()
    }

    fun clearSessionMessages() {
        _sessionMessages.value = SessionMessagesState(
            isLoading = false,
            messageEntityResp = emptyList(),
        )
        updateSelectedRecords(emptyList())
        clearSuggestionList()
    }

    fun getNewMsgId(messages: List<ChatMessage>): Int {
        if (messages.isNullOrEmpty()) {
            return 0
        }
        return messages.first().message.msgId + 1
    }

    fun getSearchResults(searchQuery: String, ownerId: String? = null) {
        if (searchQuery.isNullOrEmpty()) {
            getChatSessions(null)
            return
        }
        viewModelScope.launch {
            val response = ChatInit.getSearchResult(query = searchQuery, ownerId = ownerId)
            response?.collect { messages ->
                Log.d("getSearchResults", "getSearchResults: $messages")
                getDataChatSessions(groupBySessionId(messages))
            }
        }
    }

    private fun groupBySessionId(messages: List<MessageEntity>): List<MessageEntity> {
        val groupedMessages = messages.groupBy { it.sessionId }
        val lastMessages = mutableListOf<MessageEntity>()
        groupedMessages.forEach { (_, value) ->
            lastMessages.add(value.last())
        }
        return lastMessages
    }

    fun getChatSessions(chatContext: String?) {
        _isSessionsLoading.value = true
        viewModelScope.launch {
            var response: Response<List<MessageEntity>>? = null
            if (chatContext.isNullOrEmpty()) {
                response = ChatInit.getAllSessions(ChatUtils.getOwnerId())
            } else {
                response = ChatInit.getAllSessionByChatContext(chatContext = chatContext)
            }
            when (response) {
                is Response.Loading -> {

                }

                is Response.Success -> {
                    response.data?.let { messages ->
                        getDataChatSessions(messages)

                    }
                }

                is Response.Error -> {

                }

                else -> {}
            }
        }
    }

    private fun getDataChatSessions(messages: List<MessageEntity>) {
        val chatSessions = mutableListOf<ChatSession>()
        val deferredMessages = mutableListOf<Deferred<ChatSession>>()
        viewModelScope.launch {
            messages.forEach { message ->
                deferredMessages.add(
                    viewModelScope.async {
                        val chatMessages = ChatInit.getMessagesBySessionId(message.sessionId)
                        var totalRecords = 0
                        var totalConv = 0
                        chatMessages?.data?.firstOrNull()?.forEach {
                            totalRecords += it.messageFiles?.size ?: 0
                            if (it.msgType == MessageType.VOICE_2_RX_PRESCRIPTION_DRAFT.stringValue) {
                                val voiceSession =
                                    Voice2Rx.getSessionBySessionId(sessionId = it.messageText ?: "")
                                if (voiceSession != null) {
                                    if (voiceSession.status == Voice2RxSessionStatus.COMPLETED) {
                                        totalRecords += 1
                                    } else {
                                        totalConv += 1
                                    }
                                }
                            }
                        }

                        return@async ChatSession(
                            message = message,
                            totalConversations = totalConv,
                            totalRecords = totalRecords
                        )
                    }
                )
            }
            deferredMessages.forEach {
                chatSessions.add(it.await())
            }
            _chatSessions.value = chatSessions
            groupChatSessionByChatContext()
            _isSessionsLoading.value = false
        }
    }

    private fun groupChatSessionByChatContext() {
        val sessions = mutableMapOf<ChatContext, MutableList<ChatSession>>()
        _chatSessions.value?.forEach {
            if (!it.message.chatContext.isNullOrEmpty()) {
                try {
                    val chatContext =
                        Gson().fromJson(it.message.chatContext, ChatContext::class.java)
                    if (sessions.containsKey(chatContext)) {
                        sessions[chatContext]?.add(
                            ChatSession(
                                message = it.message,
                                totalRecords = it.totalRecords,
                                totalConversations = it.totalConversations
                            )
                        )
                    } else {
                        sessions[chatContext] = mutableListOf(it)
                    }
                } catch (e: Exception) {
                    Log.e("groupChatSessionByChatContext", "Error: ${e.message}")
                }
            }
        }
        _groupedSessionsByContext.value = sessions
    }

    fun getSessionMessages(sessionId: String) {
        if (job != null) {
            job?.cancel()
            job = null
        }
        job = viewModelScope.launch {
            val response = ChatInit.getMessagesBySessionId(sessionId = sessionId)
            if (response != null) {
                when (response) {
                    is Response.Loading -> {
                        _sessionMessages.value =
                            SessionMessagesState(isLoading = true, messageEntityResp = emptyList())
                    }

                    is Response.Success -> {
                        response.data?.collect {
                            Log.d("getSessionMessages", "getSessionMessages: $it")
                            getMessagesWithVoice2RxSession(it)
                            getChatSessions(null)
                        }
                    }

                    is Response.Error -> {
                    }

                    else -> {}
                }
            }
        }
    }

    fun getMessagesWithVoice2RxSession(messages: List<MessageEntity>) {
        val chatMessages = mutableListOf<ChatMessage>()
        val deferredMessages = mutableListOf<Deferred<ChatMessage>>()
        viewModelScope.launch {
            messages.forEach { message ->
                deferredMessages.add(
                    viewModelScope.async {
                        when (message.msgType) {
                            MessageType.VOICE_2_RX_PRESCRIPTION_COMPLETED.stringValue,
                            MessageType.VOICE_2_RX_PRESCRIPTION_DRAFT.stringValue -> {
                                val session =
                                    Voice2Rx.getSessionBySessionId(sessionId = message.messageText.toString())
                                return@async ChatMessage(
                                    message = message,
                                    voice2RxSession = session
                                )
                            }

                            else -> {
                                if (message.messageFiles.isNullOrEmpty()) {
                                    return@async ChatMessage(
                                        message = message,
                                        voice2RxSession = null
                                    )
                                } else {
                                    val files = getFilesForMessage(message)

                                    return@async ChatMessage(
                                        message = message,
                                        voice2RxSession = null,
                                        files = files
                                    )
                                }
                            }
                        }
                    }
                )
            }
            deferredMessages.forEach {
                chatMessages.add(it.await())
            }
            _sessionMessages.value =
                SessionMessagesState(
                    isLoading = false,
                    messageEntityResp = chatMessages.sortedByDescending { it.message.msgId })
        }
    }

    suspend fun getFilesForMessage(message: MessageEntity): List<RecordModel> {
        val deferredFiles = mutableListOf<Deferred<RecordModel?>>()
        val files = mutableListOf<RecordModel>()

        message.messageFiles?.forEach { fileId ->
            deferredFiles.add(
                viewModelScope.async {
                    Document.getDocumentById(id = fileId)
                }
            )
        }
        deferredFiles.forEach {
            val file = it.await()
            if (file != null) {
                files.add(file)
            }
        }

        return deferredFiles.mapNotNull { it.await() }
    }

    fun askNewQueryFireStore(
        query: String,
        chatContext: ChatContext? = null,
        ownerId: String,
        docId: String,
        userHash: String,
        selectedRecords: List<RecordModel?>
    ) {
        askNewQuery(
            query = query,
            chatContext = chatContext,
            ownerId = ownerId,
            docId = docId,
            userHash = userHash,
            selectedRecords = selectedRecords
        )
    }

    fun askNewQuery(
        query: String,
        chatContext: ChatContext? = null,
        ownerId: String,
        docId: String,
        userHash: String,
        selectedRecords: List<RecordModel?>
    ) {
        sendButtonEnabled = false
        isQueryResponseLoading = true
        val lastMsgId = _sessionMessages.value.messageEntityResp.size
        val patientId = chatContext?.patientId ?: ""
        val files = mutableListOf<String>()
        selectedRecords.forEach {
            it?.let { record ->
                files.add(record.documentId.toString())
            }
        }
        val message = MessageEntity(
            msgId = getNewMsgId(_sessionMessages.value.messageEntityResp),
            sessionId = sessionId,
            sessionIdentity = patientId,
            ownerId = ownerId,
            messageText = query,
            role = MessageRole.USER,
            msgType = MessageType.TEXT.stringValue,
            createdAt = Utils.getCurrentUTCEpochMillis(),
            chatContext = chatContext?.let { Gson().toJson(it) },
            messageFiles = files
        )
        val networkConfiguration = NetworkConfiguration(
            params = hashMapOf(
                "d_oid" to docId,
                "d_hash" to userHash,
                "pt_oid" to patientId,
                "session_id" to sessionId,
                "use_patient_data" to "true"
            ),
            baseUrl = BuildConfig.AI_CHAT_URL,
            aiBotEndpoint = "doc_chat/v1/stream_chat",
            headers = hashMapOf(),
        )
        viewModelScope.launch {
            ChatInit.askNewQuery(
                messageEntity = message,
                networkConfiguration = networkConfiguration,
            )?.collect {
                if (it.isLastEvent) {
                    sendButtonEnabled = true
                }
                if(!it.text.isNullOrEmpty()){
                    isQueryResponseLoading = false
                }
                Log.d("askNewQuery", "Event: $it")
            }
        }
    }

    fun startAudioRecording(onError: (String) -> Unit) {
        if (::audioRecorder.isInitialized) {
            audioRecorder.stopRecording()
        }
        audioRecorder = AndroidAudioRecorder(app)
        currentAudioFile = File(app.filesDir, "${Utils.getNewFileName(MessageFileType.AUDIO)}.m4a")
        audioRecorder.startRecording(currentAudioFile!!, onError = onError)
    }

    fun stopRecording() {
        if (::audioRecorder.isInitialized) {
            audioRecorder.stopRecording()
        }
        try {
            getTranscribeDataFromAudioFile(currentAudioFile!!) { response ->
                Log.d("stopRecording", "Transcribe Data: ${response.data}")
                _currentTranscribeData.value = response
            }
        } catch (e: Exception) {
            Log.e("stopRecording", "Error: ${e.message}")
            _currentTranscribeData.value = Response.Error(e.message)
        }
    }

    fun clearRecording() {
        _currentTranscribeData.value = Response.Loading()
        currentAudioFile = null
    }

    fun showToast(msg: String) {
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(app, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun getMessageEntity(
        sessionInfo: String,
        msgType: MessageType,
        msgId: Int,
    ): MessageEntity {
        return MessageEntity(
            msgId = msgId,
            sessionId = sessionId,
            sessionIdentity = consultationData?.sessionIdentity,
            ownerId = ChatUtils.getOwnerId(),
            messageText = sessionInfo,
            role = MessageRole.AI,
            msgType = msgType.stringValue,
            createdAt = Utils.getCurrentUTCEpochMillis(),
            chatContext = currentChatContext?.let { Gson().toJson(it) },
        )
    }

    fun playAudio(sessionId: String, filePath: String, onCompletion: (() -> Unit)? = null) {
        stopAudio()
        playingSessionId = sessionId
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                start()
                setOnCompletionListener {
                    onCompletion?.invoke()
                    stopAudio()
                }
            }
        } catch (e: Exception) {
            Log.d("EkaChatViewModel", "play Audio ${e.message.toString()}")
        }
    }

    fun stopAudio() {
        try {
            playingSessionId = ""
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                reset()
                release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            Log.d("EkaChatViewModel", "stop Audio ${e.message.toString()}")
        }
    }

    fun initializeVoice2RxSDK() {
        viewModelScope.launch(Dispatchers.IO) {
            (app as IAmCommon).initializeVoice2Rx(
                sessionId = "",
                context = app,
                s3Config = null,
                patientId = "",
                visitId = "",
                onStop = { sessionId, recordedFiles ->
                },
                onError = {}
            )
        }
    }

    fun startVoiceSession(
        voice2RxSessionId: String,
        visitId: String? = null,
        patientId: String? = null,
        s3Config: AwsS3ConfigResponseOld?,
        mode: Voice2RxType,
    ) {
        isVoice2RxRecording = true
        var visit = visitId
        if (visit.isNullOrEmpty()) {
            visit = voice2RxSessionId
        }
        viewModelScope.launch(Dispatchers.IO) {
            (app as IAmCommon).initializeVoice2Rx(
                sessionId = voice2RxSessionId,
                context = app,
                s3Config = AwsS3Configuration(
                    "m-prod-voice2rx",
                    s3Config?.credentials?.sessionToken ?: "",
                    s3Config?.credentials?.accessKeyId ?: "",
                    s3Config?.credentials?.secretKey ?: "",
                ),
                patientId = patientId,
                visitId = visit,
                onStop = { sessionId, recordedFiles ->
                },
                onError = {}
            )
            (app as IAmCommon).startVoiceConversation(
                sessionId = voice2RxSessionId,
                mode = mode,
            )
        }
    }

    fun getSubHeadline(session: ChatSession): String {
        return if (session.totalRecords > 0) {
            var label = "${session.totalRecords} record saved"
            if (session.totalConversations > 0) {
                label += "  •  "
            }
            label
        } else if (session.totalConversations > 0) {
            ""
        } else {
            MessageTypeMapping.getSubHeadline(session.message.msgType)
        }
    }

    private val _suggestionList = MutableStateFlow<List<SuggestionModel?>>(emptyList())
    val suggestionList = _suggestionList.asStateFlow()

    fun clearSuggestionList() {
        _suggestionList.value = emptyList()
    }


    private val pdfLibrary = GeneratePdfLibrary.getInstance()
    private var progressDialog: ProgressDialog = ProgressDialog(app)

    fun createPDF(
        context: ComponentActivity?,
        chatMessage: ChatMessage,
    ) {
        if (context == null) {
            showToast("Error generating PDF!")
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            progressDialog =
                ProgressDialog(context).apply {
                    setMessage("Generating PDF...")
                    setCancelable(false)
                    show()
                }
            val pageSize = PageSize.LEGAL
            val pixelsPerDp = context.resources.displayMetrics.density
            val pageWidth = (pageSize.width.value * pixelsPerDp).toInt()
            val pageHeight = (pageSize.height.value * pixelsPerDp).toInt()
            val composeView = withContext(Dispatchers.Main) {
                val composeView = ComposeView(context).apply {
                    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                    setViewTreeSavedStateRegistryOwner(context)
                    setViewTreeLifecycleOwner(context)
                }

                val dialog = Dialog(context).apply {
                    setContentView(composeView)
                    window?.setLayout(pageWidth, pageHeight)
                }

                composeView.setContent {
                    SharePdfContent(
                        chatMessage = chatMessage,
                        context = context,
                        onCompletion = { picture ->
                            navigateToPdfPreview(context = context, picture = picture)
                        }
                    )
                }
                dialog.show()
                delay(1000)

                composeView.measure(
                    View.MeasureSpec.makeMeasureSpec(pageWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(pageHeight, View.MeasureSpec.EXACTLY)
                )
                composeView.layout(0, 0, composeView.measuredWidth, composeView.measuredHeight)

                dialog.dismiss()
                composeView
            }
        }
    }

    private fun navigateToPdfPreview(
        context: Context,
        picture: Picture,
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    delay(3000)

                    val contentWidth = picture.width
                    val contentHeight = picture.height

                    val (pageWidth, pageHeight, scale) = pdfLibrary.getPageDimensions(
                        context = context,
                        contentWidth = contentWidth,
                        contentHeight = contentHeight
                    )

                    val canvas = PdfUtils.createBitmapFromPicture(picture)
                    val pdfDocument = PdfDocument()
                    val pageInfo =
                        PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1)
                            .create()
                    val page = pdfDocument.startPage(pageInfo)

                    val scaledWidth = contentWidth * scale
                    val scaledHeight = contentHeight * scale
                    val xOffset = (pageWidth - scaledWidth) / 2
                    val yOffset = (pageHeight - scaledHeight) / 2

                    page.canvas.save()
                    page.canvas.translate(xOffset, yOffset)
                    page.canvas.scale(scale, scale)
                    page.canvas.drawBitmap(canvas, 0f, 0f, null)
                    page.canvas.restore()

                    pdfDocument.finishPage(page)
                    val fileName = "screenshot-${System.currentTimeMillis()}.pdf"
                    val pdfFile = File(app.cacheDir, fileName)

                    pdfDocument.writeTo(FileOutputStream(pdfFile))
                    pdfDocument.close()

                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        (app as IAmCommon).navigateTo(
                            context as Activity,
                            PageIdentifier.PAGE_PDF_PREVIEW_SCREEN,
                            JSONObject().apply {
                                put("filePath", pdfFile.absolutePath)
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        app,
                        "Error generating PDF: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (::audioRecorder.isInitialized) {
            audioRecorder.stopRecording()
        }
        isVoice2RxRecording = false
        stopAudio()
    }
}