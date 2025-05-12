package eka.dr.intl.assistant.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.eka.conversation.ui.presentation.components.BorderCard
import dev.jeziellago.compose.markdowntext.MarkdownText
import eka.dr.intl.assistant.presentation.models.ChatMessage
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.typography.touchBodyRegular

@Composable
fun ChatBubbleRight(
    chatMessage: ChatMessage,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(top = 4.dp)
    ) {
        BorderCard(
            modifier = Modifier
                .weight(1f)
                .padding(start = 36.dp),
            border = BorderStroke(width = 0.dp, color = Color.Transparent),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 0.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            onClick = onClick,
            content = {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarwinTouchNeutral0)
                ) {
                    items(chatMessage.files) { recordModel ->
                        recordModel?.let { record ->
                            AsyncImage(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(color = DarwinTouchNeutral1000)
                                    .graphicsLayer(alpha = 0.4f),
                                model = record.thumbnail,
                                contentDescription = "Thumbnail",
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }
                MarkdownText(
                    markdown = chatMessage.message.messageText.toString(),
                    modifier = Modifier.padding(16.dp),
                    style = touchBodyRegular,
                    color = DarwinTouchNeutral800
                )
            },
            background = Color.White
        )
    }
}
