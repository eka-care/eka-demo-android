package eka.dr.intl.assistant.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral600
import eka.dr.intl.ekatheme.color.DarwinTouchYellowBgLight
import eka.dr.intl.ekatheme.color.DarwinTouchYellowDark
import eka.dr.intl.icons.R
import eka.dr.intl.typography.touchFootnoteRegular
import eka.dr.intl.typography.touchLabelRegular

@Composable
fun ChatBubbleRecordingComponent(
    topStartPadding: Dp,
    containerColor: Color,
    icon: Int,
    iconTint: Color,
    title: String,
    titleStyle: TextStyle,
    subtitle: String? = null,
    tagEnabled: Boolean = false,
    tagBackgroundColor: Color = DarwinTouchYellowBgLight,
    tagTextColor: Color = DarwinTouchYellowDark,
    date: String,
    tagText: String? = null,
    borderStroke: BorderStroke? = null,
    onClick: () -> Unit = {},
    isFirstMessage: Boolean = false
) {
    val iconAlpha = if (isFirstMessage) 1f else 0f
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Start
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

        Card(
            modifier = Modifier
                .width(200.dp)
                .clickable {
                    onClick()
                },
            border = borderStroke,
            shape = RoundedCornerShape(
                topEnd = 16.dp,
                bottomEnd = 16.dp,
                bottomStart = 16.dp,
                topStart = topStartPadding
            ),
            colors = CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = DarwinTouchNeutral1000
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, top = 10.dp, end = 16.dp, bottom = 10.dp),
                verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = "null",
                    modifier = Modifier
                        .size(16.dp),
                    colorFilter = ColorFilter.tint(color = iconTint)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(text = title, color = DarwinTouchNeutral1000, style = titleStyle)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!tagEnabled) {
                            if (subtitle != null) {
                                Text(
                                    text = "${subtitle} • ",
                                    color = DarwinTouchNeutral600,
                                    style = touchFootnoteRegular
                                )
                            }
                        } else {
                            if (!tagText.isNullOrEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            tagBackgroundColor
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (tagText != null) {
                                        Text(
                                            text = "${tagText}",
                                            style = touchLabelRegular,
                                            color = tagTextColor
                                        )
                                    }
                                }
                            }
                        }
                        if (date.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(1.dp))
                            Text(
                                text = "$date", color = DarwinTouchNeutral600,
                                style = touchFootnoteRegular
                            )
                        }
                    }
                }
            }
        }
    }

}