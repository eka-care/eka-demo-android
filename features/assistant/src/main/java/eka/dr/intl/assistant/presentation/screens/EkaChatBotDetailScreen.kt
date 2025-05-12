package eka.dr.intl.assistant.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import eka.dr.intl.assistant.presentation.components.ChatSessionsEmptyState
import eka.dr.intl.assistant.presentation.components.EkaChatPatientsRow
import eka.dr.intl.assistant.presentation.components.EkaChatPatientsRowData
import eka.dr.intl.assistant.presentation.viewmodel.EkaChatViewModel
import eka.dr.intl.assistant.utility.ChatUtils
import eka.dr.intl.assistant.utility.MessageType
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.Urls
import eka.dr.intl.common.data.dto.response.ChatContext
import eka.dr.intl.common.utility.ProfileHelper
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral100
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral300
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral50
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral600
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgLight
import eka.dr.intl.ekatheme.color.DarwinTouchYellowDark
import eka.dr.intl.icons.R
import eka.dr.intl.typography.touchBodyRegular
import eka.dr.intl.typography.touchCalloutBold
import eka.dr.intl.ui.atom.IconWrapper
import eka.dr.intl.ui.custom.ProfileImage
import eka.dr.intl.ui.custom.ProfileImageProps
import eka.dr.intl.ui.molecule.IconButtonWrapper
import eka.dr.intl.ui.organism.AppBarCustom
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EkaChatBotDetailScreen(
    openDrawer: () -> Unit = {},
    viewModel: EkaChatViewModel,
    onPatientChatClick: (ChatContext) -> Unit,
    navigateToChatScreen: (String) -> Unit,
    onEmptyScreen: () -> Unit,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    var searchText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val isSessionLoading by viewModel.isSessionLoading.collectAsState()
    val loggedInUser = OrbiUserManager.getUserTokenData()?.oid
    val name = OrbiUserManager.getUserTokenData()?.name
    val docProfilePic = Urls.DOC_PROFILE_URL + loggedInUser
    val selectedOption = viewModel.botViewMode.collectAsState().value

    val onOptionSelected: (BotViewMode) -> Unit = {
        val params = JSONObject()
        params.put("type", it.type)
        viewModel.updateBotViewMode(it)
    }
    val options = listOf(
        EkaBotModeData(
            BotViewMode.PATIENT,
            "Patients",
        ),
        EkaBotModeData(BotViewMode.ALL_CHATS, "All chats")
    )
    val listState = rememberLazyListState()
    val chatSessions by viewModel.chatSessions.collectAsState()
    val sessionByContext by viewModel.groupedSessionsByContext.collectAsState()

    LaunchedEffect(searchText) {
        if (searchText.isEmpty()) {
            viewModel.getChatSessions(chatContext = null)
        } else {
            viewModel.getSearchResults(searchQuery = searchText, ownerId = ChatUtils.getOwnerId())
        }
    }
    LaunchedEffect(chatSessions) {
        if (chatSessions.isNotEmpty()) {
            listState.animateScrollToItem(chatSessions?.size!! - 1)
        } else if (chatSessions.isNullOrEmpty() && !isSessionLoading && searchText.isNullOrEmpty()) {
            onEmptyScreen.invoke()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = DarwinTouchNeutral100,
        topBar = {
            AppBarCustom(
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(48.dp)
                            .clickable(
                                onClick = openDrawer,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true)
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
                title = {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .width(screenWidth.times(0.65f))
                            .height(48.dp)
                    ) {
                        options.forEachIndexed { index, option ->
                            SegmentedButton(
                                colors = SegmentedButtonDefaults.colors().copy(
                                    activeBorderColor = DarwinTouchPrimary,
                                    activeContainerColor = DarwinTouchPrimaryBgLight,
                                    activeContentColor = DarwinTouchPrimary,
                                    inactiveBorderColor = DarwinTouchNeutral300,
                                    inactiveContainerColor = DarwinTouchNeutral0,
                                    inactiveContentColor = DarwinTouchNeutral1000
                                ),
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = options.size
                                ),
                                onClick = { onOptionSelected(option.type) },
                                selected = selectedOption == option.type,
                            ) {
                                Text(
                                    text = option.title,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(padding),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                if ((!sessionByContext.isNullOrEmpty()) || selectedOption == BotViewMode.ALL_CHATS) {
                    TextField(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50))
                            .onFocusChanged {
                                if (it.isFocused) {
                                    val params = JSONObject()
                                    params.put("type", "search")
                                }
                            }
                            .focusRequester(focusRequester),
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            viewModel.getSearchResults(
                                searchQuery = searchText,
                                ownerId = ChatUtils.getOwnerId()
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DarwinTouchNeutral50,
                            unfocusedContainerColor = DarwinTouchNeutral50,
                            focusedTextColor = DarwinTouchNeutral1000,
                            cursorColor = DarwinTouchNeutral1000,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = {
                            Text(
                                text = "Search chat...",
                                style = touchBodyRegular,
                                color = DarwinTouchNeutral600
                            )
                        },
                        leadingIcon = {
                            IconWrapper(
                                icon = R.drawable.ic_magnifying_glass_regular,
                                contentDescription = "Search",
                                tint = DarwinTouchNeutral800,
                            )
                        },
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButtonWrapper(
                                    icon = R.drawable.ic_circle_xmark_solid,
                                    contentDescription = "Clear",
                                    onClick = { searchText = "" }
                                )
                            }
                        },
                        maxLines = 1,
                        singleLine = true
                    )
                }
                when (selectedOption) {
                    BotViewMode.PATIENT -> {
                        if (sessionByContext.isNullOrEmpty()) {
                            ChatSessionsEmptyState(padding)
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                itemsIndexed(sessionByContext.toList()) { index, message ->
                                    var subHeadline = "${message.second.size} chats"
                                    val totalRecords = message.second.sumOf { it.totalRecords }
                                    if (totalRecords > 0) {
                                        subHeadline += " • $totalRecords records"
                                    }
                                    EkaChatPatientsRow(
                                        data = EkaChatPatientsRowData(
                                            headlineText = message.first.patientName,
                                            subHeadline = subHeadline,
                                            time = ChatUtils.getTimeStampString(message.second.first().message.createdAt),
                                            spaceBetweenSubHeadlineAndTime = false
                                        ),
                                        onClick = {
                                            onPatientChatClick.invoke(message.first)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    BotViewMode.ALL_CHATS -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(chatSessions) { _, message ->
                                var headline = when (message.message.msgType) {
                                    MessageType.TEXT.stringValue -> message.message.messageText
                                    else -> "Conversation"
                                }
                                if (headline.isNullOrEmpty()) {
                                    headline = "Conversation"
                                }
                                if (message.message.chatContext != null) {
                                    val chatContext =
                                        ChatUtils.getChatContextFromString(message.message.chatContext)
                                    EkaChatPatientsRow(
                                        data = EkaChatPatientsRowData(
                                            headlineText = headline,
                                            subHeadline = viewModel.getSubHeadline(message),
                                            draftNumber = message.totalConversations,
                                            draftNumberColor = DarwinTouchYellowDark,
                                            icon = R.drawable.ic_messages_regular,
                                            time = ChatUtils.getTimeStampString(message.message.createdAt),
                                            spaceBetweenSubHeadlineAndTime = true,
                                            nameTag = chatContext?.patientName
                                        ),
                                        onClick = {
                                            navigateToChatScreen(message.message.sessionId)
                                        }
                                    )
                                } else {
                                    EkaChatPatientsRow(
                                        data = EkaChatPatientsRowData(
                                            headlineText = headline,
                                            icon = R.drawable.ic_messages_regular,
                                            draftNumber = message.totalConversations,
                                            draftNumberColor = DarwinTouchYellowDark,
                                            subHeadline = viewModel.getSubHeadline(message),
                                            time = ChatUtils.getTimeStampString(message.message.createdAt),
                                            spaceBetweenSubHeadlineAndTime = true,
                                        ),
                                        onClick = {
                                            navigateToChatScreen(message.message.sessionId)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 80.dp),
                onClick = {
                    val params = JSONObject()
                    params.put("type", "start_new_chat")
                    navigateToChatScreen.invoke(ChatUtils.getNewSessionId())
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = DarwinTouchPrimaryBgLight
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconWrapper(
                        icon = R.drawable.ic_pen_solid,
                        tint = DarwinTouchNeutral1000,
                        contentDescription = "New Chat",
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "New",
                        style = touchCalloutBold,
                        color = DarwinTouchNeutral1000
                    )
                }
            }
        }
    )
}

enum class BotViewMode(val type: String) {
    PATIENT("patients"),
    ALL_CHATS("all_chats")
}

data class EkaBotModeData(
    val type: BotViewMode,
    val title: String
)