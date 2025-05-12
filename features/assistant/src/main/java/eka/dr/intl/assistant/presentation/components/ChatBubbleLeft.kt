package eka.dr.intl.assistant.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.eka.conversation.ui.presentation.components.BorderCard
import dev.jeziellago.compose.markdowntext.MarkdownText
import eka.dr.intl.icons.R
import eka.dr.intl.assistant.presentation.models.ChatMessage
import eka.dr.intl.assistant.utility.ActionType
import eka.dr.intl.assistant.utility.MessageType
import eka.dr.intl.common.data.dto.response.CTA
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.typography.touchBodyRegular
import eka.dr.intl.ui.molecule.IconButtonWrapper

@Composable
fun ChatBubbleLeft(
    message: ChatMessage,
    value: String,
    onClick: (CTA) -> Unit,
    showResponseButtons: Boolean = true,
    isFirstMessage: Boolean = false
) {
    val iconAlpha = if (isFirstMessage) 1f else 0f
    Column {
        Row(
            modifier = Modifier
                .padding(top = 4.dp)
                .clickable {
                    onClick(CTA(action = ActionType.ON_CHAT_MESSAGE_CLICKED.stringValue))
                }
        ) {
            Icon(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .alpha(iconAlpha)
                    .size(32.dp),
                painter = painterResource(id = R.drawable.ic_ai_chat_custom),
                tint = Color.Unspecified,
                contentDescription = ""
            )
            BorderCard(
                modifier = Modifier.weight(1f),
                border = BorderStroke(width = 0.dp, color = Color.Transparent),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                content = {
                    Column {
                        MarkdownText(
                            modifier = Modifier
                                .padding(start = 0.dp, top = 0.dp, end = 16.dp, bottom = 16.dp),
                            markdown = value,
                            truncateOnTextOverflow = true,
                            enableSoftBreakAddsNewLine = true,
                            style = touchBodyRegular,
                            color = DarwinTouchNeutral1000
                        )
                    }
                },
                background = Color.Transparent
            )
        }
        if (message.message.msgType == MessageType.TEXT.stringValue && showResponseButtons) {
            Row(
                modifier = Modifier
                    .padding(start = 24.dp, top = 4.dp)
            ) {
//                IconButtonWrapper(
//                    onClick = {
//                        onClick(CTA(action = ActionType.ON_SHARE_CLICKED.stringValue))
//                    },
//                    icon = R.drawable.ic_share_nodes_regular,
//                    contentDescription = "Share",
//                    iconSize = 20.dp
//                )
                IconButtonWrapper(
                    onClick = {
                        onClick(CTA(action = ActionType.ON_COPY_CLICKED.stringValue))
                    },
                    icon = R.drawable.ic_copy_regular,
                    contentDescription = "Copy",
                    iconSize = 20.dp
                )
            }
        }
    }
}