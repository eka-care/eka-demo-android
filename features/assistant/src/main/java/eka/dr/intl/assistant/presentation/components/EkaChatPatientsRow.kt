package eka.dr.intl.assistant.presentation.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eka.dr.intl.common.V2RxUtils
import eka.dr.intl.ekatheme.color.Blue50
import eka.dr.intl.ekatheme.color.DarwinTouchGreen
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral600
import eka.dr.intl.ekatheme.color.FuchsiaViolet50
import eka.dr.intl.typography.touchBodyRegular
import eka.dr.intl.typography.touchCalloutBold
import eka.dr.intl.typography.touchCalloutRegular
import eka.dr.intl.typography.touchHeadlineBold
import eka.dr.intl.typography.touchLabelRegular

data class EkaChatPatientsRowData(
    val nameTag: String? = null,
    val headlineText: String? = null,
    val subHeadline: String? = null,
    val draftNumber: Int? = null,
    val draftNumberColor: Color? = null,
    val spaceBetweenSubHeadlineAndTime: Boolean,
    val time: String? = null,
    val icon: Int? = null,
    val iconSize: Int? = null,
    val iconColor : Color? = null,
    val backgroundColor: Color? = null
)

@Composable
fun EkaChatPatientsRow(data: EkaChatPatientsRowData, onClick: () -> Unit) {
    val colors = V2RxUtils.generateRandomColor(data.headlineText ?: "")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentAlignment = if (data.backgroundColor == Color.White) {
                Alignment.TopCenter
            } else {
                Alignment.Center
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .let {
                        if (data.icon != null && data.headlineText != null && data.nameTag != null) {
                            it.background(
                                brush = Brush.horizontalGradient(
                                    colorStops = arrayOf(
                                        0.3f to Blue50,
                                        1f to FuchsiaViolet50
                                    )
                                )
                            )
                        } else {
                            BackgroundColor(data, it, colors)
                        }
                    }
            )
            InitialsOrIcon(data = data, color = colors)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            NameText(data = data)
            Spacer(modifier = Modifier.height(4.dp))
            SubHeadlineAndTimeRow(data = data)
        }
    }
}

@Composable
private fun BackgroundColor(
    data: EkaChatPatientsRowData,
    it: Modifier,
    colors: Pair<Color, Color>
) = if (data.backgroundColor != null) {
    it.background(color = data.backgroundColor)
} else {
    it.background(color = colors.first)
}

@Composable
fun InitialsOrIcon(data: EkaChatPatientsRowData, color: Pair<Color, Color>) {
    val initials = getInitials(data)
    if (data.icon != null && data.nameTag == null) {
        Icon(
            painter = painterResource(id = data.icon),
            contentDescription = null,
            tint = data.iconColor ?: DarwinTouchNeutral1000,
            modifier = Modifier.size(data.iconSize?.dp ?: 16.dp)
        )
    } else if (initials != null) {
        Text(
            text = initials,
            color = color.second,
            style = touchHeadlineBold
        )
    }
}

@Composable
fun getInitials(data: EkaChatPatientsRowData): String? {
    val name = data.nameTag ?: data.headlineText
    val textToUse = name?.takeIf { it.isNotEmpty() } ?: return null

    return textToUse.split(" ")
        .filter { it.isNotEmpty() }
        .map { it.first().uppercaseChar() }
        .take(2)
        .joinToString("")
}

@Composable
fun NameText(data: EkaChatPatientsRowData) {
    when {
        !data.headlineText.isNullOrEmpty() -> {
            Text(
                text = data.headlineText.split("\n")
                    .joinToString("\n") { line ->
                        line.trim().replaceFirstChar { it.uppercaseChar() }
                    },
                color = DarwinTouchNeutral1000,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = touchBodyRegular
            )
        }
    }
}

@Composable
fun SubHeadlineAndTimeRow(data: EkaChatPatientsRowData) {
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.Start) {
        Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
            if (data.nameTag != null) {
                Text(
                    text = data.nameTag,
                    style = touchCalloutBold,
                    color = DarwinTouchNeutral1000
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            SubHeadlineAndDraft(data = data)
        }
        if (!data.spaceBetweenSubHeadlineAndTime) {
            Spacer(modifier = Modifier.width(4.dp))
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        data.time?.let {
            Text(
                text = it,
                color = DarwinTouchNeutral600,
                style = touchLabelRegular
            )
        }
    }
}

@Composable
fun SubHeadlineAndDraft(data: EkaChatPatientsRowData) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
        data.subHeadline?.let {
            Text(
                text = it,
                style = touchCalloutRegular,
                color = DarwinTouchNeutral600
            )
        }
        data.draftNumber?.takeIf { it > 0 }?.let {
            Text(
                text = "$it draft${if (it > 1) "s" else ""}",
                style = touchCalloutRegular,
                color = data.draftNumberColor ?: DarwinTouchGreen
            )
        }
    }
}