package eka.dr.intl.assistant.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.icons.R
import eka.dr.intl.typography.touchBodyRegular
import kotlinx.coroutines.delay

@Composable
fun ChatBubbleProcessing() {
    var dotCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(true) {
        while (true) {
            delay(500)
            dotCount = (dotCount + 1) % 4
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            modifier = Modifier
                .padding(end = 8.dp)
                .size(32.dp),
            painter = painterResource(id = R.drawable.ic_ai_chat_custom),
            tint = Color.Unspecified,
            contentDescription = ""
        )
        Text(
            text = buildString {
                repeat(dotCount) {
                    append(".")
                }
            },
            style = touchBodyRegular,
            color = DarwinTouchNeutral800
        )
    }
}