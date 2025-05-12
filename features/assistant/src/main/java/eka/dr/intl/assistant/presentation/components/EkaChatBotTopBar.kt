package eka.dr.intl.assistant.presentation.components

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eka.dr.intl.icons.R
import eka.dr.intl.assistant.utility.ActionType
import eka.dr.intl.common.data.dto.response.CTA
import eka.dr.intl.ekatheme.color.Blue50
import eka.dr.intl.ekatheme.color.FuchsiaViolet100
import eka.dr.intl.ui.molecule.IconButtonWrapper
import eka.dr.intl.ui.organism.AppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EkaChatBotTopBar(
    hasClinicalNotes: Boolean,
    consultationStarted: Boolean = false,
    title: String,
    subTitle: String? = null,
    onClick: (CTA) -> Unit
) {
    val backgroundColor = if (!consultationStarted) {
        null
    } else {
        Color.White
    }
    val backgroundBrush = if (!consultationStarted) {
        Brush.horizontalGradient(
            colorStops = arrayOf(
                0.4f to Blue50,
                1f to FuchsiaViolet100
            )
        )
    } else {
        null
    }
    AppBar(
        borderColor = Color.Transparent,
        modifier = Modifier.then(
            if (!consultationStarted) {
                Modifier.background(brush = backgroundBrush!!)
            } else {
                Modifier.background(backgroundColor!!)
            }
        ),
        containerColor = Color.Transparent,
        title = title.replaceFirstChar { it.uppercaseChar() },
        subTitle = subTitle,
        navigationIcon = {
            IconButtonWrapper(
                onClick = { onClick(CTA(action = ActionType.ON_BACK.stringValue)) },
                icon = R.drawable.ic_arrow_left_regular,
                iconSize = 16.dp
            )
        },
        actions = {
            if (hasClinicalNotes) {
                IconButtonWrapper(
                    icon = R.drawable.ic_file_waveform_regular,
                    onClick = { onClick(CTA(action = ActionType.OPEN_CLINICAL_NOTES.stringValue)) },
                    iconSize = 16.dp
                )
            }
        }
    )
}
